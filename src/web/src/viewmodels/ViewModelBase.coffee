define ["jquery", "knockout", "EventService"], ($, ko, events) ->
  class ViewModelBase
    # called from the ViewManager after creating the view model
    init: () => @done()

    # called from the ViewManager before releasing the view model
    deinit: () => @done()

    # called when the view model has come into view of the user
    activate: () =>

    # called before the view model goes out of view of the user
    deactivate: () =>

    # knockout helper methods
    observable: (value) -> ko.observable(value)
    observableArray: (array) -> ko.observableArray(array)

    # event helper methods
    emitMessage: (type, message) -> events.emit("messages", type, { type: type, message: message})
    emitEvent: (namespace, name, data) -> events.emit(namespace, name, data)
    listenEvent: (namespace, name, callback) -> events.listen(namespace, name, callback)

    # promise helper methods
    done: () -> $.Deferred().resolve().promise()
