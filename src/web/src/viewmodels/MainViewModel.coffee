define ["EventService", "ViewModelBase"], (events, ViewModelBase) ->
  class MainViewModel extends ViewModelBase
    init: () =>
      @isAuthenticated = @observable(false)
      @messages = @observableArray([])
      events.listen("messages", "*", (message) => @messages.push(message))
      @done()

