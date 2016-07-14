class @Channel

    constructor: (_url, _useWebSockets, _socketsUrl, _logger, _settings) ->
        @url = _url
        @useWebSockets = _useWebSockets
        @socketsUrl = _socketsUrl
        if _logger? and _logger.debug? and _logger.error? and _logger.info?
            @logger = _logger
        else
            if console? and console.debug? and console.error? and console.info?
                @logger = console
            else
                @logger = {}
                @logger.debug = () ->
                    return
                @logger.info = () ->
                    return
                @logger.error = () ->
                    return
        if _settings?
            if _settings.heartBeatInterval?
                @heartBeatInterval = _settings.heartBeatInterval
            else
                @heartBeatInterval = 5000
            if _settings.disconnectTimeout?
                @disconnectTimeout = _settings.disconnectTimeout
            else
                @disconnectTimeout = 30000
            if _settings.compressionEnabled?
                @compressionEnabled = _settings.compressionEnabled
            else
                @compressionEnabled = false
        @outputMessages = []
        @inputMessages = []
        @sentMessages = []
        @socket = null
        @pollingRequest = null
        @httpSendRequest = null
        @eventHandlers = {}
        @connectAtemps = 0
        @pollingAborted = false # Global stop flag
        @channelId = Util.get().generateId()
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
        @recovered = true
        @from = 0
        @to = 0

    sendHeartBeat: () =>
        @sendRequest MessageFactory.get().create('HeartBeat')
        delta = Date.now() - @lastSendTime
        @logger.debug @.toString() + ' Send HeartBeat after ' + delta + ' ms inactivity'
        return
    
    checkConnection: () =>
        @disconnect(true)
        @connect(true)
        @sendRequest MessageFactory.get().create('TestRequest')
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
            handler() for handler in @errorHandlers
            @curStatus = 'error' 
        return
        
    onSuccess: (uniqId, data) =>
        @logger.debug @.toString() + ' Polling request success. Id = ' + uniqId
        @connectAtemps = 0 
        @ioInterval = 100
        if @curStatus != 'success' # to prevent success spamming
            handler() for handler in @successHandlers
            @curStatus = 'success'
        @processMessage message for message in data    
        return
        
    sendNewPollingRequest: () =>
        if @pollingAborted == false && @pollingRequest == null
            try
                @httpSendTask.schedule @ioInterval
                uniqId = @id++
                @logger.debug @.toString() + ' Send new polling request. Atempt = ' + @connectAtemps + '. Id =' + uniqId
                message = MessageFactory.get().create('PollingRequest')
                http = new XMLHttpRequest()
                #if http.responseType?
                #    compressionSupported = true
                #else
                compressionSupported = false
                msg = JSON.stringify [{
                    seqnum: -1,
                    message: message
                }]
                protocol = if 'https:' == document.location.protocol then 'https://' else 'http://'
                http.open('POST', protocol + window.location.host + @url + '?channelId=' + @channelId + '&compressionSupported=' + compressionSupported, true)
                if @compressionEnabled and compressionSupported
                    http.responseType = 'arraybuffer'
                    msg = pako.deflate msg
                http.timeout = 30000;
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
            if @socketsUrl? then addPath = @socketsUrl else addPath = @url
            protocol = if 'https:' == document.location.protocol then 'wss://' else 'ws://'
            if 'WebSocket' of window
                try
                    socket = new WebSocket(protocol + window.location.host + addPath + '?channelId=' + @channelId)
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
                        handler() for handler in @successHandlers
                        @curStatus = 'success'
                    @websocketSendTask.schedule @ioInterval
                    return
                socket.onclose = () =>
                    @logger.debug @.toString() + ' Socket closed. Id = ' + uniqId
                    @connectAtemps++
                    if @socket == socket
                        @socket = null
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
                socket.onerror = () =>
                    @logger.error @.toString() + ' Socket error. Id = ' + uniqId
                    if @curStatus != 'error'
                        handler() for handler in @errorHandlers
                        @curStatus = 'error'
                    return
                @socket = socket
        return
        
    dispacthEvent: (event, val) =>
        if event of @eventHandlers
            handler val for handler in @eventHandlers[event]
        return
     
    processMessage: (data) =>
        message = data.message
        seqnum = data.seqnum
        expectedSeqnum = @inputSeqnum + 1
        if seqnum == expectedSeqnum
            if @isRecovered()
                @handleMessage message
            else
                @stash data
        else if seqnum > expectedSeqnum
            @logger.info @.toString() + ' Missed messages between ' + @inputSeqnum + ' and ' + seqnum
            resendRequest = MessageFactory.get().create('ResendRequest')
            resendRequest.from = @inputSeqnum
            resendRequest.to = seqnum
            @sendRequest resendRequest
            @initRecover expectedSeqnum, seqnum
            @stash data
        else if seqnum < expectedSeqnum
            if @isRecovered()
                @logger.info @.toString() + ' Unexpected message with seqnum ' + seqnum + ', expected seqnum ' + expectedSeqnum
            else
                @stash data
                @tryRecover()
        if seqnum > @inputSeqnum
            @inputSeqnum = seqnum
        return

    stash: (data) =>
        @logger.info @.toString() + ' Stash message with seqnum ' + data.seqnum
        pos = data.seqnum - @from
        @inputMessages[pos] = data
        return
                    
    isRecovered: () =>
        return @recovered;

    initRecover: (from, to) =>
        @logger.info @.toString() + ' Recover init'
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
            @handleMessage @inputMessages[0].message
            @from = @inputMessages[0].seqnum
            @inputMessages.splice 0, 1
        if @inputMessages.length == 0 and @from >= @to
            @logger.info @.toString() + ' Recover complete'
            @recovered = true
        return

    handleMessage: (message) =>
        @lastUpdateTime = Date.now()
        @checkConnectionTask.delay @heartBeatInterval * 2
        @reconnectTask.delay @disconnectTimeout
        if message['messageType'] == 'HeartBeat'
            @logger.debug @.toString() + 'HeartBeat received'
        else if message['messageType'] == 'TestRequest'
            @logger.debug @.toString() + ' Test request received'
            @sendRequest MessageFactory.get().create('HeartBeat')
        else if message['messageType'] == 'ResendRequest'
            @logger.info @.toString() + ' Resend messages from ' + message.from + ' to ' + message.to
            toResend = @getSentMessages message.from, message.to
            if toResend.length + 1 < message.to - message.from
                @logger.error @.toString() + ' failed to resend messages from ' + message.from + ' to ' + message.to
                @disconnect()
            else    
                for request in toResend
                    @outputMessages.push request
                if @socket?
                    @websocketSendTask.schedule @ioInterval
                else
                    @httpSendTask.schedule @ioInterval
        else
            try
                @dispacthEvent 'on' + message['messageType'], message
            catch e
                @logger.error @.toString() + ' ' + e.message, e
            return
        
    connect: (soft = false) =>
        @logger.info @.toString() + ' connecting'
        @pollingAborted = false
        @curStatus = null
        if @useWebSockets and 'WebSocket' of window
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
            @successHandlers.splice i, 1 for func, i in @successHandlers when handler == func
        else if event == 'error'
            @successHandlers.splice i, 1 for func, i in @errorHandlers when handler == func
        else
            if event of @eventHandlers
                @eventHandlers[event].splice i, 1 for func, i in @eventHandlers[event] when handler == func
        return
        
    httpSend: () =>
        if @pollingAborted == false and @outputMessages.length > 0
            try
                http = new XMLHttpRequest()
                #if http.responseType?
                #    compressionSupported = true
                #else
                compressionSupported = false
                tmp = @outputMessages
                msg = JSON.stringify tmp
                @outputMessages = []
                protocol = if 'https:' == document.location.protocol then 'https://' else 'http://'
                http.open('POST', protocol + window.location.host + @url + '?channelId=' + @channelId + '&compressionSupported=' + compressionSupported, true)
                if @compressionEnabled and compressionSupported
                    http.responseType = 'arraybuffer'
                    msg = pako.deflate msg
                http.timeout = 30000;
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
        messages.push data for data in @sentMessages when data.seqnum > from and data.seqnum < to
        return messages
    
    rememberMessage: (data) =>
        if @sentMessages.length > 1000
            @sentMessages.shift()
        @sentMessages.push data
        return
        
    toString: () =>
        return 'Channel[channelId=' + @channelId + ']'