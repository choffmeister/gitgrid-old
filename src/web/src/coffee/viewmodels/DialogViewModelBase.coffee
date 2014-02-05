define ["jquery", "ViewModelBase"], ($, ViewModelBase) ->
  class DialogViewModelBase extends ViewModelBase
    constructor: () ->
      @deferred = $.Deferred()

    close: (result) => @deferred.resolve(result)
    cancel: () => @deferred.reject()
    result: () => @deferred.promise()
