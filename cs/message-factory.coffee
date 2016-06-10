class AbstractMessage
    
    constructor: (_messageType) ->
        @messageType = _messageType
    
class @MessageFactory
    
    instance = null
    
    @get: () ->
        instance ?= new _MessageFactory()
    
class _MessageFactory
    
    constructor: () ->

    create: (_messageType) ->
        return new AbstractMessage(_messageType)