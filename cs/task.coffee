class @Task

    constructor: (_task, _repeat = false) ->
        @task = () =>
            if not _repeat
                @taskId = null
            _task()
            return
        @repeat = _repeat
        @taskId = null

    schedule: (_timeout) ->
        if not @taskId?
            if @repeat == true
                @taskId = setInterval(@task, _timeout)
            else
                @taskId = setTimeout(@task, _timeout)
        return

    delay: (_timeout) ->
        @cancel()
        if @repeat == true
            @taskId = setInterval(@task, _timeout)
        else
            @taskId = setTimeout(@task, _timeout)
        return

    cancel: () ->
        if @taskId?
            if @repeat == true
                clearInterval(@taskId)
            else
                clearTimeout(@taskId)
            @taskId = null
        return
