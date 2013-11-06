define ["jquery", "DialogViewModelBase"], ($, DialogViewModelBase) ->
  class LoginDialogViewModel extends DialogViewModelBase
    init: () =>
      @busy = @observable(false)
      @focus = @observable(null)
      @userName = @observable("")
      @password = @observable("")
      @message =
        visible: @observable(false)
        style: @observable(null)
        text: @observable(null)
      @done()

    activate: () =>
      @prepare()
      @done()

    deactivate: () =>
      @prepare()
      @done()

    prepare: () =>
      @password("")
      @busy(false)
      @focus(null)
      @focus(if not @userName() then "userName" else "password")

    showMessage: (style, text) ->
      @message.style(style)
      @message.text(text)
      @message.visible(true)

    login: () =>
      @busy(true)

      if @userName() and @password()
        @authenticate(@userName(), @password())
          .done (result) =>
            if result is true
              @close(true)
            else
              @showMessage("warning", "User name or password incorrect!")
              @prepare()
          .fail (err) =>
            @showMessage.style("danger", "An error occured while trying to authenticate!")
            @prepare()
      else
        @showMessage("info", "Please enter both, your user name and your password.")
        @prepare()

    authenticate: (userName, password) =>
      deferred = $.Deferred()
      deferred.resolve(userName == password)
      return deferred.promise()
