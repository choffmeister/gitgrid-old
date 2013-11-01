define ["ViewModelBase"], (ViewModelBase) ->
  class DashboardViewModel extends ViewModelBase
    init: () =>
      @name = @observable("foo")
