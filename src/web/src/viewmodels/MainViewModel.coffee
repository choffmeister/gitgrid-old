define ["ViewModelBase", "LoginDialogViewModel"], (ViewModelBase, LoginDialogViewModel) ->
  class MainViewModel extends ViewModelBase
    init: (viewManager) =>
      @viewManager = viewManager
      @isAuthenticated = @observable(false)
      @messages = @observableArray([])
      @busy = @observable(false)
      @listenEvent("messages", "*", (message) => @messages.push(message))
      @listenEvent("viewmanager", "loadingview", (loading) => @busy(loading))
      @done()

    login: () =>
      @viewManager.loadDialogView(true, "logindialog", LoginDialogViewModel).done (result) =>
        @isAuthenticated(true) if result is true

    logout: () =>
      @isAuthenticated(false)
