define ["jquery", "knockout", "auth", "vm", "ViewModelBase", "viewmodels/LoginDialogViewModel"], ($, ko, auth, vm, ViewModelBase, LoginDialogViewModel) ->
  class MainViewService extends ViewModelBase
    init: () =>
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
      @listenEvent("global", "keydown", (event) => @clearNotifications() if event.keyCode == 27)
      ko.applyBindings(this, $("body").get(0))
      @done()

    login: () =>
      vm.loadDialogView(true, "logindialog", LoginDialogViewModel)

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
    showNotification: (elem) -> $(elem).css({ opacity: 0, scale: 0.5 }).transition({ opacity: 1, scale: 1 }, 500)
    hideNotification: (elem) -> $(elem).transition({ opacity: 0, scale: 1.5 }, 500, () -> $(elem).remove())
    clearNotifications: () => @notifications.removeAll()

  return new MainViewService()
