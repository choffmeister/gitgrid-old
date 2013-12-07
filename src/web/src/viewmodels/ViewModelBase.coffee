define ["jquery", "knockout", "events"], ($, ko, events) ->
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
    computed: (fn) -> ko.computed(fn)

    # event helper methods
    emitEvent: (namespace, name, data) -> events.emit(namespace, name, data)
    listenEvent: (namespace, name, callback) -> events.listen(namespace, name, callback)

    # notification helper methods
    notifySuccess: (title, message) => @emitEvent("notification", "success", { title: title, message: message })
    notifyInfo: (title, message) => @emitEvent("notification", "info", { title: title, message: message })
    notifyWarning: (title, message) => @emitEvent("notification", "warning", { title: title, message: message })
    notifyError: (title, message) => @emitEvent("notification", "error", { title: title, message: message })

    # promise helper methods
    done: () -> $.Deferred().resolve().promise()

    # validate view model
    validate: () =>
      errors = ko.validation.group(this)
      errors.showAllMessages()
      errors().length == 0
