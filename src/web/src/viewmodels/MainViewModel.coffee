define ["ViewModelBase"], (ViewModelBase) ->
  class MainViewModel extends ViewModelBase
    init: () =>
      @isAuthenticated = @observable(false)
