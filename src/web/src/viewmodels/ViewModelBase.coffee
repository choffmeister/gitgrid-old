define ["knockout"], (ko) ->
  class ViewModelBase
    init: () =>
      # subclasses override this method

    observable: (value) => ko.observable(value)
    observableArray: (array) => ko.observable(array)
