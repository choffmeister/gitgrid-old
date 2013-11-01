define [], () ->
  class EventService
    constructor: () ->
      @listeners = {}
      @nextListenerId = 1
      @pushClientId = null

      @registerDomEvents()

    listen: (namespace, name, callback) =>
      fqen = "#{namespace}:#{name}";

      # make sure that the listeners list is not null
      if not this.listeners[fqen]
        @listeners[fqen] = []

      # get new unique listener id
      listenerId = @nextListenerId++

      # if there is a callback then add it to listeners
      @listeners[fqen].push({ id: listenerId, callback: callback })

      # return listener id
      listenerId

    unlisten: (listenerId) =>
      # search all namespaces and all listeners for id and remove it if found
      for fqen, listeners of @listeners
        for listener, i in listeners
          if listener.id == listenerId
            listeners.splice i, 1
            return true
      false

    emit: (namespace, name, data) =>
      fqen = "#{namespace}:#{name}"
      fqenMulti = "#{namespace}:*"

      # TODO: remove
      console.log fqen, data if name.substr(0,4) != 'tick'

      listeners = @listeners[fqen] ? []
      for listener in listeners
        listener.callback(data)
      listeners = @listeners[fqenMulti] ? []
      for listener in listeners
        listener.callback(data)

    registerDomEvents: () =>
      $(window).resize((event) => @emit("global", "resize", event))
      $(window).keydown((event) => @emit("global", "keydown", event))
      $(window).keypress((event) => @emit("global", "keypress", event))
      $(window).keyup((event) => @emit("global", "keyup", event))

      @globalTick(100)
      @globalTick(1000)
      @globalTick(5000)
      @globalTick(10000)

    globalTick: (frequence) =>
      @emit("global", "tick#{frequence}", null);
      window.setTimeout (() =>
        @globalTick(frequence)), frequence

  return new EventService()
