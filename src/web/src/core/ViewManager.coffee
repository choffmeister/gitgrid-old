define ["jquery", "knockout", "HttpService", "MainViewModel"], ($, ko, http, MainViewModel) ->
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
      if viewModel?
        try
          viewModel.init()
        catch ex
          $.Deferred().reject(ex).promise()
      else
        $.Deferred().resolve().promise()

    deinitViewModel: (viewModel) =>
      if viewModel?
        try
          viewModel.deinit()
        catch ex
          $.Deferred().reject(ex).promise()
      else
        $.Deferred().resolve().promise()

    applyViewModel: (viewModel) =>
      if viewModel?
        ko.applyBindings(viewModel, @content.get(0))
        @viewModel = viewModel

    unapplyViewModel: () =>
      @content.find("*").each () ->
        $(this).unbind()
        ko.removeNode(this)
      ko.cleanNode(@content.get(0))
      @viewModel = null

    loadTemplate: (templateName) =>
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
