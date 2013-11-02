define ["jquery", "knockout", "EventService"], ($, ko, events) ->
  class ViewModelBase
    # called from the ViewManager before (eventually) applying the view
    # must return a promise to indicate when initialization is done
    init: () => @done()

    # called from the ViewManager before (eventually) unapplying the view
    # here is the place to persist remaining data to the server
    # note, that calling this method does not mean that the view model is acutally unloaded
    # must return a promise to indicate when deinitialization is done
    deinit: () => @done()

    # knockout helper methods
    observable: (value) -> ko.observable(value)
    observableArray: (array) -> ko.observableArray(array)

    # event helper methods
    emitMessage: (type, message) -> events.emit("messages", type, { type: type, message: message})
    emitEvent: (namespace, name, data) -> events.emit(namespace, name, data)
    listenEvent: (namespace, name, callback) -> events.listen(namespace, name, callback)

    # promise helper methods
    done: () -> $.Deferred().resolve().promise()
