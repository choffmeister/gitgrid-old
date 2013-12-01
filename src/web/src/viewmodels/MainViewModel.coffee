define ["jquery", "auth", "ViewModelBase", "viewmodels/LoginDialogViewModel"], ($, auth, ViewModelBase, LoginDialogViewModel) ->
  class MainViewModel extends ViewModelBase
    init: (viewManager) =>
      @viewManager = viewManager
      @isAuthenticated = @observable(false)
      @messages = @observableArray([])
      @notifications = @observableArray([])
      @busy = @observable(false)
      @userDisplayName = @observable(null)
      @listenEvent("messages", "*", (message) => @messages.push(message))
      @listenEvent("viewmanager", "loadingview", (loading) => @busy(loading))
      @listenEvent("auth", "changestate", @authStateChanged)
      @listenEvent("notification", "success", (data) => @addNotification("success", data.title, data.message))
      @listenEvent("notification", "info", (data) => @addNotification("info", data.title, data.message))
      @listenEvent("notification", "warning", (data) => @addNotification("warning", data.title, data.message))
      @listenEvent("notification", "error", (data) => @addNotification("danger", data.title, data.message))
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

    addNotification: (type, title, message) => @notifications.push({ type: type, title: title, message: message })
    showNotification: (elem) -> $(elem).hide().fadeIn(200)
    hideNotification: (elem) -> $(elem).fadeOut(200, () -> $(elem).remove())
    clearNotifications: () => @notifications.removeAll()
