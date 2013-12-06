define ["jquery", "ViewModelBase"], ($, ViewModelBase) ->
  class WidgetTestViewModel extends ViewModelBase
    init: () ->
      @userName = @observable("user")
      @description = @observable("My description.")
      @birthday = @observable("01.02.2013")

      @json = @computed () =>
        JSON.stringify({
          userName: @userName()
          description: @description()
          birthday: @birthday()
        }, true, 4)
