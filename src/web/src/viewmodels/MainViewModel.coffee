define ["auth", "ViewModelBase", "LoginDialogViewModel"], (auth, ViewModelBase, LoginDialogViewModel) ->
  class MainViewModel extends ViewModelBase
    init: (viewManager) =>
      @viewManager = viewManager
      @isAuthenticated = @observable(false)
      @messages = @observableArray([])
      @busy = @observable(false)
      @userDisplayName = @observable(null)
      @listenEvent("messages", "*", (message) => @messages.push(message))
      @listenEvent("viewmanager", "loadingview", (loading) => @busy(loading))
      @listenEvent("auth", "changestate", @authStateChanged)
      @done()

    login: () =>
      @viewManager.loadDialogView(true, "logindialog", LoginDialogViewModel)

    logout: () =>
      auth.unauthenticate()

    authStateChanged: (user) =>
      if user?
        @isAuthenticated(true)
        @userDisplayName(user.userName)
      else
        @isAuthenticated(false)
        @userDisplayName(null)
