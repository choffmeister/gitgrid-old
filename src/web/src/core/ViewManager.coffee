define ["jquery", "knockout", "Logger", "HttpService", "MainViewModel"], ($, ko, log, http, MainViewModel) ->
  class ViewManager
    constructor: () ->
      @templateCache = {}
      @viewModel = null
      @loading = false

    init: () =>
      $(document).ready () =>
        @body = $("body")
        @content = $("#content")
        @mainViewModel = new MainViewModel()
        @mainViewModel.init()
        ko.applyBindings(@mainViewModel, @body.get(0))

    loadView: (templateName, viewModelType) =>
      log.debug("Load view", templateName, viewModelType)
      @loading = true
      newViewModel = if viewModelType? then new viewModelType() else null

      $.when(@loadTemplate(templateName), @initViewModel(newViewModel), @deinitViewModel(@viewModel))
        .done (template) =>
          @unapplyViewModel()
          @content.html(template)
          @applyViewModel(newViewModel)
        .fail (err) =>
          console.log(err.responseText)
        .always () =>
          @loading = false

    initViewModel: (viewModel) =>
      log.debug("Init view model", viewModel)
      if viewModel?
        try
          viewModel.init()
        catch ex
          $.Deferred().reject(ex).promise()
      else
        $.Deferred().resolve().promise()

    deinitViewModel: (viewModel) =>
      log.debug("Deinit view model", viewModel)
      if viewModel?
        try
          viewModel.deinit()
        catch ex
          $.Deferred().reject(ex).promise()
      else
        $.Deferred().resolve().promise()

    applyViewModel: (viewModel) =>
      log.debug("Apply view model", viewModel)
      if viewModel?
        ko.applyBindings(viewModel, @content.get(0))
        @viewModel = viewModel

    unapplyViewModel: () =>
      log.debug("Unapply view model", @viewModel)
      @content.find("*").each () ->
        $(this).unbind()
        ko.removeNode(this)
      ko.cleanNode(@content.get(0))
      @viewModel = null

    loadTemplate: (templateName) =>
      log.debug("Load template #{templateName}")
      deferred = $.Deferred()

      template = @templateCache[templateName]
      if not template?
        http.get("/views/#{templateName}.html")
          .done (template) =>
            @templateCache[templateName] = template
            deferred.resolve(template)
          .fail (error) =>
            deferred.reject(error)
      else
        deferred.resolve(template)

      return deferred
