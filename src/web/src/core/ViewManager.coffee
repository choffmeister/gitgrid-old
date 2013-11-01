define ["jquery", "knockout", "HttpService", "MainViewModel"], ($, ko, http, MainViewModel) ->
  class ViewManager
    constructor: () ->
      @templateCache = {}

    init: () =>
      $(document).ready () =>
        @body = $("body")
        @content = $("#content")
        @mainViewModel = new MainViewModel()
        @mainViewModel.init()
        ko.applyBindings(@mainViewModel, @body.get(0))

    loadView: (templateName, viewModelType) =>
      @loadTemplate(templateName)
        .done (template) =>
          if viewModelType?
            viewModel = new viewModelType()
            viewModel.init()

            @unapplyViewModel()
            @content.html(template)
            @applyViewModel(viewModel)
          else
            @unapplyViewModel()
            @content.html(template)
        .fail (error) =>
          console.log error
          window.alert "Error while loading view template"

    applyViewModel: (viewModel) =>
      ko.applyBindings(viewModel, @content.get(0))

    unapplyViewModel: () =>
      @content.find("*").each () ->
        $(this).unbind()
        ko.removeNode(this)
      ko.cleanNode(@content.get(0))

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
