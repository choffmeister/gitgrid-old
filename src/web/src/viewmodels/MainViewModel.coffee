define ["EventService", "ViewModelBase", "LoginDialogViewModel"], (events, ViewModelBase, LoginDialogViewModel) ->
  class MainViewModel extends ViewModelBase
    init: (viewManager) =>
      @viewManager = viewManager
      @isAuthenticated = @observable(false)
      @messages = @observableArray([])
      events.listen("messages", "*", (message) => @messages.push(message))
      @done()

    login: () =>
      @viewManager.loadDialogView(true, "logindialog", LoginDialogViewModel).done (result) =>
        @isAuthenticated(true) if result is true

    logout: () =>
      @isAuthenticated(false)
