define ["jquery", "ViewModelBase"], ($, ViewModelBase) ->
  class ThrowingViewModel extends ViewModelBase
    init: () ->
      deferred = $.Deferred()
      deferred.reject("Some fanzy error occured")
      deferred.promise()
