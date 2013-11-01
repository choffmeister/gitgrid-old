define ["ViewModelBase"], (ViewModelBase) ->
  class TestViewModel extends ViewModelBase
    init: () =>
      @name = @observable("foo")
