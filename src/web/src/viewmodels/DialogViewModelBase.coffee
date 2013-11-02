define ["jquery", "ViewModelBase"], ($, ViewModelBase) ->
  class DialogViewModelBase extends ViewModelBase
    constructor: () ->
      @deferred = $.Deferred()

    # called when corresponding dialog is in view
    visible: () =>

    close: (result) => @deferred.resolve(result)
    cancel: () => @deferred.reject()
    result: () => @deferred.promise()
