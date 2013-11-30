define ["jquery", "api", "vm", "ViewModelBase"], ($, api, vm, ViewModelBase) ->
  class CreateTicketViewModel extends ViewModelBase
    init: () =>
      @name = @observable().extend({ required: true, maxLength: 10 })
      @description = @observable().extend({ required: true, maxLength: 1024 })
      @done()

    submit: () =>
      if @validate()
        vm.showNotificationSuccess(@name() + " " + @description())
      else
        vm.showNotificationError("Validation errors")
