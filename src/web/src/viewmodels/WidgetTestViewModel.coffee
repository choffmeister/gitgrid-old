define ["jquery", "ViewModelBase"], ($, ViewModelBase) ->
  class WidgetTestViewModel extends ViewModelBase
    init: () ->
      @foo = @observableArray(["1", "2", "3"])
      @userName = @observable("user")
      @description = @observable("My description.")
      @birthday = @observable("01.02.2013")
      @tags = @observableArray(["scala", "coffeescript"])
      @group = @observable(2)
      @group2 = @observable("scala")

      @json = @computed () =>
        JSON.stringify({
          userName: @userName()
          description: @description()
          birthday: @birthday()
          tags: @tags()
          group: @group()
          group2: @group2()
        }, true, 4)
