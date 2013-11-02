define ["DialogViewModelBase"], (DialogViewModelBase) ->
  class LoginDialogViewModel extends DialogViewModelBase
    init: () =>
      @busy = @observable(false)
      @focus = @observable(null)
      @userName = @observable("")
      @password = @observable("")
      @message = @observable(null)
      @done()

    visible: () =>
      @focus("userName")

    login: () =>
      @busy(true)
      window.setTimeout () =>
        window.alert "#{@userName()} -> #{@password()}"
        @busy(false)
        @password("")
        @focus(null)
        @focus(if not @userName() then "userName" else "password")
      , 1000

