define ["knockout", "EventService"], (ko, events) ->
  class ViewModelBase
    init: () =>
      # subclasses override this method

    # knockout helper methods
    observable: (value) => ko.observable(value)
    observableArray: (array) => ko.observableArray(array)

    # event helper methods
    emitMessage: (type, message) => events.emit("messages", type, { type: type, message: message})
    emitEvent: (namespace, name, data) => events.emit(namespace, name, data)
    listenEvent: (namespace, name, callback) => events.listen(namespace, name, callback)
