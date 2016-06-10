class @Util
    
    instance = null
    
    @get: () ->
        instance ?= new _Util()
    
class _Util
    
    constructor: () ->
        return
    
    S4 = () ->
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1)
    
    generateId: () ->
        return S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4()
    	