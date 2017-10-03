class @Channel

    constructor: (_url, _socketsUrl, _logger, _settings) ->
        @url = _url
        @socketsUrl = _socketsUrl
        if not @socketsUrl?
            @socketsUrl = @url
        if _logger?
            @logger = _logger
        else
            if console?
                @logger = console
            else
                @logger = {}
        if not @logger.debug?
            @logger.debug = () ->
                return
        if not @logger.info?
            @logger.info = () ->
                return
        if not @logger.error?
            @logger.error = () ->
                return
        if not @logger.log?
            @logger.log = () ->
                return
        @channelId = Util.get().generateId()
        @maxDispatchTime = 1000
        @heartBeatInterval = 5000
        @disconnectTimeout = 30000
        @compressionEnabled = false
        if _settings?
            if _settings.heartBeatInterval?
                @heartBeatInterval = _settings.heartBeatInterval
            if _settings.disconnectTimeout?
                @disconnectTimeout = _settings.disconnectTimeout
            if _settings.compressionEnabled?
                @compressionEnabled = _settings.compressionEnabled
            if _settings.idPrefix?
                @channelId = _settings.idPrefix + '-' + @channelId
        if @heartBeatInterval < @maxDispatchTime
            @maxDispatchTime = @heartBeatInterval
        @outputMessages = []
        @inputMessages = []
        @sentMessages = []
        @receivedMessages = []
        @socket = null
        @pollingRequest = null
        @httpSendRequest = null
        @eventHandlers = {}
        @connectAtemps = 0
        @pollingAborted = false # Global stop flag
        @successHandlers = []
        @errorHandlers = []
        @id = 1 # XMLHttpRequest / Websocket counter
        @outputSeqnum = 1
        @inputSeqnum = 0
        @ioInterval = 100
        @curStatus = null
        @lastSendTime = Date.now()
        @lastUpdateTime = @lastSendTime
        @sendHeartBeatTask = new Task @sendHeartBeat
        @checkConnectionTask = new Task @checkConnection
        @reconnectTask = new Task @reconnect
        @sendNewPollingRequestTask = new Task @sendNewPollingRequest
        @sendNewSocketRequestTask = new Task @sendNewSocketRequest
        @httpSendTask = new Task @httpSend
        @websocketSendTask = new Task @websocketSend
        @dispacthTask = new Task @dispacthEvent
        @resendRequestTask = new Task @resendRequest
        @recovered = true
        @from = 0
        @to = 0

    sendHeartBeat: () =>
        @sendRequest MessageFactory.get().create('com.exactprosystems.webchannels.messages.HeartBeat')
        delta = Date.now() - @lastSendTime
        @logger.debug @.toString() + ' Send HeartBeat after ' + delta + ' ms inactivity'
        return

    checkConnection: () =>
        @sendRequest MessageFactory.get().create('com.exactprosystems.webchannels.messages.TestRequest')
        delta = Date.now() - @lastUpdateTime
        @logger.info @.toString() + ' Send TestRequest after ' + delta + ' ms inactivity'
        return

    reconnect: () =>
        @disconnect()
        delta = Date.now() - @lastUpdateTime
        @logger.info @.toString() + ' Disconnect after ' + delta + ' ms inactivity'
        return

    onComplete: (uniqId, http) =>
        @logger.debug @.toString() + ' Polling request complete. Id = ' + uniqId
        if @pollingRequest == http
            @pollingRequest = null
        @sendNewPollingRequestTask.schedule @ioInterval
        if @ioInterval < 1000
            @ioInterval = @ioInterval + 100
        return

    onError: (uniqId) =>
        @logger.error @.toString() + ' Polling request error. Id = ' + uniqId
        @connectAtemps++
        if @curStatus != 'error' # to prevent error spamming
            for handler in @errorHandlers
                remove = handler()
                if remove
                    @removeHandler 'error', handler
            @curStatus = 'error'
        return

    onSuccess: (uniqId, data) =>
        @logger.debug @.toString() + ' Polling request success. Id = ' + uniqId
        @connectAtemps = 0
        @ioInterval = 100
        if @curStatus != 'success' # to prevent success spamming
            for handler in @successHandlers
                remove = handler()
                if remove
                    @removeHandler 'success', handler
            @curStatus = 'success'
        @processMessage message for message in data
        return

    sendNewPollingRequest: () =>
        if @pollingAborted == false && @pollingRequest == null
            try
                @httpSendTask.schedule @ioInterval
                uniqId = @id++
                @logger.debug @.toString() + ' Send new polling request. Atempt = ' + @connectAtemps + '. Id =' + uniqId
                message = MessageFactory.get().create('com.exactprosystems.webchannels.messages.PollingRequest')
                http = new XMLHttpRequest()
                if http.responseType?
                    compressionSupported = true
                else
                    compressionSupported = false
                msg = JSON.stringify [{
                    seqnum: -1,
                    message: message
                }]
                http.open('POST', @url + '?channelId=' + @channelId + '&compressionSupported=' + compressionSupported, true)
                if @compressionEnabled and compressionSupported
                    http.responseType = 'arraybuffer'
                    msg = pako.deflate msg
                http.timeout = @heartBeatInterval * 2;
                http.onreadystatechange = () =>
                    if http.readyState == 4 and http.status == 200
                        if @compressionEnabled and compressionSupported
                            if http.response?
                                try
                                    unzip = pako.inflate http.response, { to: 'string' }
                                    data = JSON.parse unzip
                                    @onSuccess uniqId, data
                                catch e
                                    @logger.error @.toString() + ' ' + e, e
                        else
                            if http.responseText?
                                try
                                    data = JSON.parse http.responseText
                                    @onSuccess uniqId, data
                                catch e
                                    @logger.error @.toString() + ' ' + e, e
                        @onComplete uniqId, http
                    if http.readyState == 4 and http.status != 200
                        @onError uniqId
                        @onComplete uniqId, http
                    return
                http.send msg
                @pollingRequest = http
            catch e
                @logger.error @.toString() + ' Can not open polling request: ' + e.message, e
        return

    sendNewSocketRequest: () =>
        if @pollingAborted == false and @socket == null
            uniqId = @id++
            @logger.info @.toString() + ' Send new socket request. Atempt = ' + @connectAtemps + '. Id = ' + uniqId
            if 'WebSocket' of window
                try
                    socket = new WebSocket(@socketsUrl + '?channelId=' + @channelId)
                catch e
                    @logger.error @.toString() + ' Can not open websocket: ' + e.message, e
            if socket?
                if @compressionEnabled
                    socket.binaryType = 'arraybuffer'
                socket.onopen = () =>
                    @logger.debug @.toString() + ' Socket opened success. Id = ' + uniqId
                    @connectAtemps = 0
                    @ioInterval = 100
                    if @curStatus != 'success'
                        for handler in @successHandlers
                            remove = handler()
                            if remove
                                @removeHandler 'success', handler
                        @curStatus = 'success'
                    @websocketSendTask.schedule @ioInterval
                    return
                socket.onclose = () =>
                    @logger.debug @.toString() + ' Socket closed. Id = ' + uniqId
                    @connectAtemps++
                    if @socket == socket
                        @socket = null
                    if uniqId == 1 and @inputSeqnum == 0
                        @sendNewPollingRequestTask.schedule @ioInterval
                        @sendNewSocketRequestTask.cancel()
                    else
                        @sendNewSocketRequestTask.schedule @ioInterval
                        if @ioInterval < 1000
                            @ioInterval = @ioInterval + 100
                    return
                socket.onmessage = (stream) =>
                    if stream.data?
                        try
                            if @compressionEnabled
                                unzip = pako.inflate stream.data, {to: 'string'}
                                packet = JSON.parse unzip
                            else
                                packet = JSON.parse stream.data
                            @processMessage message for message in packet
                        catch e
                            @logger.error @.toString() + ' ' + e.message, e
                    return
                socket.onerror = (e) =>
                    @logger.error @.toString() + ' Socket error: ' + e + '. Id = ' + uniqId
                    if @curStatus != 'error'
                        for handler in @errorHandlers
                            remove = handler()
                            if remove
                                @removeHandler 'error', handler
                        @curStatus = 'error'
                    return
                @socket = socket
            else
                @sendNewPollingRequestTask.schedule @ioInterval
                @sendNewSocketRequestTask.cancel()
        return

    dispacthEvent: () =>
        startTime = Date.now()
        while  Date.now() - startTime < @maxDispatchTime and @receivedMessages.length > 0
            message = @receivedMessages.shift()
            event = message['messageType']
            @logger.debug @.toString() + ' Dispatch event: ' + event
            try
                if event of @eventHandlers
                    for handler in @eventHandlers[event]
                        remove = handler message
                        if remove
                            @removeHandler event, handler
            catch e
                @logger.error @.toString() + ' ' + e.message, e
        if @receivedMessages.length > 0
            @dispacthTask.schedule 10
        return

    processMessage: (data) =>
        message = data.message
        seqnum = data.seqnum
        expectedSeqnum = @inputSeqnum + 1
        @handleAdminMessage message
        if seqnum == expectedSeqnum
            if @isRecovered()
                @handleBusinessMessage message
            else
                @logger.info @.toString() + ' Stash message with seqnum ' + data.seqnum
                @stash data
        else if seqnum > expectedSeqnum
            @logger.info @.toString() + ' Missed messages from ' + expectedSeqnum + ' to ' + seqnum
            if expectedSeqnum == 1
                @disconnect()
                throw new Error("Connection closed on client")
            resendRequest = MessageFactory.get().create('com.exactprosystems.webchannels.messages.ResendRequest')
            resendRequest.from = expectedSeqnum
            resendRequest.to = seqnum
            @sendRequest resendRequest
            @resendRequestTask.schedule @heartBeatInterval
            @logger.info @.toString() + ' Recover init'
            @initRecover expectedSeqnum, seqnum
            @logger.info @.toString() + ' Stash message with seqnum ' + data.seqnum
            @stash data
        else if seqnum < expectedSeqnum
            if @isRecovered()
                @logger.info @.toString() + ' Unexpected message with seqnum ' + seqnum + ', expected seqnum ' + expectedSeqnum
            else
                @logger.info @.toString() + ' Stash message with seqnum ' + data.seqnum
                @stash data
                @tryRecover()
                if @isRecovered()
                    @logger.info @.toString() + ' Recover complete'
        if seqnum > @inputSeqnum
            @inputSeqnum = seqnum
        if @isRecovered()
            @lastUpdateTime = Date.now()
            @checkConnectionTask.delay @heartBeatInterval * 2
            @reconnectTask.delay @disconnectTimeout
        return

    stash: (data) =>
        pos = data.seqnum - @from
        if pos >= 0
            @inputMessages[pos] = data
        return

    isRecovered: () =>
        return @recovered;

    initRecover: (from, to) =>
        if @recovered == true
            @recovered = false
            @from = from
            @to = to
            @inputMessages = []
        else
            if from < @from
                @from = from
            if to > @to
                @to = to
        return

    tryRecover: () =>
        while @inputMessages[0]?
            data = @inputMessages.shift()
            @handleBusinessMessage data.message
            @from = data.seqnum + 1
        if @inputMessages.length == 0 and @from >= @to
            @recovered = true
        return

    resendRequest: () =>
        if not @isRecovered()
            @logger.info @.toString() + ' Try to recover messages from ' + @from + ' to ' + @to + ' again'
            resendRequest = MessageFactory.get().create('com.exactprosystems.webchannels.messages.ResendRequest')
            resendRequest.from = @from
            resendRequest.to = @to
            @sendRequest resendRequest
            @resendRequestTask.schedule @heartBeatInterval
        return

    handleAdminMessage: (message) =>
        if message['messageType'] == 'com.exactprosystems.webchannels.messages.HeartBeat'
            @logger.debug @.toString() + 'HeartBeat received'
        else if message['messageType'] == 'com.exactprosystems.webchannels.messages.TestRequest'
            @logger.info @.toString() + ' TestRequest received'
            @sendRequest MessageFactory.get().create('com.exactprosystems.webchannels.messages.HeartBeat')
        else if message['messageType'] == 'com.exactprosystems.webchannels.messages.ResendRequest'
            @logger.info @.toString() + ' ResendRequest received from ' + message.from + ' to ' + message.to
            if message.from == 1
                @disconnect()
                throw new Error("Connection closed on server")
            toResend = @getSentMessages message.from, message.to
            if toResend.length < message.to - message.from
                @disconnect()
                throw new Error(@.toString() + ' failed to resend messages from ' + message.from + ' to ' + message.to)
            else
                for request in toResend
                    @logger.info @.toString() + ' Resend message with seqnum ' + request.seqnum
                    @outputMessages.push request
                if @socket?
                    @websocketSendTask.schedule @ioInterval
                else
                    @httpSendTask.schedule @ioInterval
        else if message['messageType'] == 'CloseChannel'
            @logger.info @.toString() + ' CloseChannel received'
            @disconnect()
            throw new Error("Connection closed on server")
        return

    handleBusinessMessage: (message) =>
        if message['messageType'] != 'com.exactprosystems.webchannels.messages.HeartBeat' and message['messageType'] != 'com.exactprosystems.webchannels.messages.TestRequest' and message['messageType'] != 'com.exactprosystems.webchannels.messages.ResendRequest'
            @receivedMessages.push message
            @dispacthTask.schedule 10
        return

    connect: (soft = false) =>
        @logger.info @.toString() + ' connecting'
        @pollingAborted = false
        @curStatus = null
        if @socketsUrl and 'WebSocket' of window
            @sendNewSocketRequestTask.schedule @ioInterval
        else
            @sendNewPollingRequestTask.schedule @ioInterval
        if soft == false
            @reconnectTask.delay @disconnectTimeout
            @checkConnectionTask.delay @heartBeatInterval * 2
            @sendHeartBeatTask.delay @heartBeatInterval
        return

    disconnect: (soft = false) =>
        @logger.info @.toString() + ' disconnecting'
        @pollingAborted = true
        @curStatus = null
        if @socket?
            try
                @socket.close()
            catch e
                @logger.error @.toString() + ' ' + e.message, e
            @socket = null
        if @pollingRequest?
            try
                @pollingRequest.abort()
            catch e
                @logger.error @.toString() + ' ' + e.message, e
            @pollingRequest = null
        if @httpSendRequest?
            try
                @httpSendRequest.abort()
            catch e
                @logger.error @.toString() + ' ' + e.message, e
            @httpSendRequest = null
        if soft == false
            @reconnectTask.cancel()
            @checkConnectionTask.cancel()
            @sendHeartBeatTask.cancel()
            @sendNewPollingRequestTask.cancel()
            @sendNewSocketRequestTask.cancel()
            @httpSendTask.cancel()
            @websocketSendTask.cancel()
            @dispacthTask.cancel()
            @resendRequestTask.cancel()
        return

    addHandler: (event, handler) =>
        if event == 'success'
            @successHandlers.push handler
        else if event == 'error'
            @errorHandlers.push handler
        else
            if event not of @eventHandlers
                @eventHandlers[event] = []
            @eventHandlers[event].push handler
        return

    removeHandler: (event, handler) =>
        if event == 'success'
            @successHandlers.splice i, 1 for fn, i in @successHandlers when fn == handler
        else if event == 'error'
            @errorHandlers.splice i, 1 for fn, i in @errorHandlers when fn == handler
        else
            if event of @eventHandlers
                @eventHandlers[event].splice i, 1 for fn, i in @eventHandlers[event] when handler == fn
        return

    httpSend: () =>
        if @pollingAborted == false and @outputMessages.length > 0
            try
                http = new XMLHttpRequest()
                if http.responseType?
                    compressionSupported = true
                else
                    compressionSupported = false
                tmp = @outputMessages
                msg = JSON.stringify tmp
                @outputMessages = []
                http.open('POST', @url + '?channelId=' + @channelId + '&compressionSupported=' + compressionSupported, true)
                if @compressionEnabled and compressionSupported
                    http.responseType = 'arraybuffer'
                    msg = pako.deflate msg
                http.timeout = @heartBeatInterval;
                http.onreadystatechange = () =>
                    if http.readyState == 4
                        if http.status != 200
                            tmp.reverse()
                            for data in tmp
                                @outputMessages.unshift data
                        else
                            @lastSendTime = Date.now()
                            @sendHeartBeatTask.delay @heartBeatInterval
                        @httpSendTask.schedule @ioInterval
                    return
                http.send msg
                @httpSendRequest = http
            catch e
                @logger.error @.toString() + ' Can not send message request ' + e.message, e
        return

    websocketSend: () =>
        try
            if @pollingAborted == false and @outputMessages.length > 0
                if @socket? and @socket.readyState == 1
                    msg = JSON.stringify @outputMessages
                    if @compressionEnabled
                        msg = pako.deflate msg
                    @socket.send msg
                    @outputMessages = []
                    @lastSendTime = Date.now()
                    @sendHeartBeatTask.delay @heartBeatInterval
                else
                    @websocketSendTask.schedule @ioInterval
        catch e
            @logger.error @.toString() + ' Can not send message request ' + e.message, e
        return

    sendRequest: (message) =>
        data =
            message: message,
            seqnum: @outputSeqnum++
        @rememberMessage data
        @outputMessages.push data
        if @socket?
            @websocketSendTask.schedule @ioInterval
        else if @pollingRequest
            @httpSendTask.schedule @ioInterval
        return

    getSentMessages: (from, to) =>
        messages = []
        messages.push data for data in @sentMessages when data.seqnum >= from and data.seqnum < to
        return messages

    rememberMessage: (data) =>
        if @sentMessages.length > 1000
            @sentMessages.shift()
        @sentMessages.push data
        return

    toString: () =>
        return 'Channel[channelId=' + @channelId + ']'
