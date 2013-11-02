define ["jquery", "bootstrap", "knockout", "LoggerService", "HttpService", "MainViewModel"], ($, bs, ko, log, http, MainViewModel) ->
  class ViewManagerService
    constructor: () ->
      @templateCache = {}
      @viewModel = null
      @loading = false

    init: () =>
      $(document).ready () =>
        @body = $("body")
        @content = $("#content")
        @dialogs = $("#dialogs")
        @mainViewModel = new MainViewModel()
        @mainViewModel.init(this)
        ko.applyBindings(@mainViewModel, @body.get(0))

    loadView: (templateName, viewModelType) =>
      log.debug("Load view", templateName, viewModelType)
      @loading = true

      # instantiate view model if type was specified
      newViewModel = if viewModelType? then new viewModelType() else null

      $.when(@loadTemplate(templateName), @initViewModel(newViewModel), @deinitViewModel(@viewModel))
        .done (template) =>
          # unapply old view model, inject template into DOM and apply new view model
          @unapplyViewModel(@content)
          @content.html(template)
          @applyViewModel(newViewModel, @content)

        .fail (err) =>
          log.error("Error while loading view", err)

        .always () =>
          @loading = false

    createDialogView: (modal, templateName, viewModelType) =>
      log.debug("Create dialog view", templateName, viewModelType)

      # instantiate view model if type was specified
      dialogViewModel = if viewModelType? then new viewModelType() else null

      # load template and initialize view model
      $.when(@loadTemplate(templateName), @initViewModel(dialogViewModel))
        .done (template) =>
          # wrap dialog template and append to DOM
          dialog = $(template)
          wrapper = $("<div></div>").append(dialog)
          @dialogs.append(wrapper)

          # apply view model
          @applyViewModel(dialogViewModel, dialog)

          # show dialog
          dialog.modal({ backdrop: modal })

          # register to view models result promise and hide dialog if promise is resolved or rejected
          if dialogViewModel?
            dialogViewModel.result().always () =>
              dialog.modal("hide")

          # register to dialogs hide event and remove dialog from DOM after closing
          dialog.on "hidden.bs.modal", () =>
            @unapplyViewModel(dialog)
            wrapper.remove()

          # register to dialogs show event and notify view model when the view is ready and in place
          dialog.on "shown.bs.modal", () =>
            dialogViewModel.visible()

        .fail (err) =>
          log.error("Error while creating dialog view", err)

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

    applyViewModel: (viewModel, wrapper) =>
      log.debug("Apply view model", viewModel)
      if viewModel?
        ko.applyBindings(viewModel, wrapper.get(0))
        @viewModel = viewModel

    unapplyViewModel: (wrapper) =>
      log.debug("Unapply view model", @viewModel)
      wrapper.find("*").each () ->
        $(this).unbind()
        ko.removeNode(this)
      ko.cleanNode(wrapper.get(0))
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

  return new ViewManagerService()
