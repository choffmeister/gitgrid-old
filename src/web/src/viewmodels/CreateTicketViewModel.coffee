define ["jquery", "api", "ViewModelBase"], ($, api, ViewModelBase) ->
  class CreateTicketViewModel extends ViewModelBase
    init: () =>
      @name = @observable().extend({ required: true, maxLength: 10 })
      @description = @observable().extend({ required: true, maxLength: 1024 })
      @done()

    submit: () =>
      if @validate()
        window.alert(@name() + " " + @description())
      else
        window.alert("Validation errors")
