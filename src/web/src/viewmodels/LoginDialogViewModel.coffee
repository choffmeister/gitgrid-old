define ["jquery", "DialogViewModelBase"], ($, DialogViewModelBase) ->
  class LoginDialogViewModel extends DialogViewModelBase
    init: () =>
      @busy = @observable(false)
      @focus = @observable(null)
      @userName = @observable("")
      @password = @observable("")
      @message = @observable(null)
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

    login: () =>
      @busy(true)

      if @userName() and @password()
        @authenticate(@userName(), @password())
          .done (result) =>
            if result is true
              @close(true)
            else
              @message({ style: "warning", message: "User name or password incorrect!" })
              @prepare()
          .fail (err) =>
            @message({ style: "danger", message: "An error occured while trying to authenticate!" })
            @prepare()
      else
        @message({ style: "info", message: "Please enter both, your user name and your password." })
        @prepare()

    authenticate: (userName, password) =>
      deferred = $.Deferred()
      deferred.resolve(userName == password)
      return deferred.promise()
