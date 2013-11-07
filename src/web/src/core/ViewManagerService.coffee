define ["jquery", "bootstrap", "knockout", "log", "events", "http", "MainViewModel"], ($, bs, ko, log, events, http, MainViewModel) ->
  class ViewManagerService
    constructor: () ->
      @templateCache = {}
      @viewModel = null
      @loading = false

    init: () =>
      @body = $("body")
      @content = $("#content")
      @dialogs = $("#dialogs")
      @mainViewModel = new MainViewModel()
      @mainViewModel.init(this)
      ko.applyBindings(@mainViewModel, @body.get(0))

    loadView: (templateName, viewModelType) =>
      if @loading is false
        log.debug("Load view", templateName, viewModelType)
        @loading = true
        events.emit("viewmanager", "loadingview", true)

        # instantiate view model if type was specified
        oldViewModel = @viewModel
        newViewModel = if viewModelType? then new viewModelType() else null

        $.when(@loadTemplate(templateName), @initViewModel(newViewModel))
          .done (template) =>
            # swap views
            oldViewModel.deactivate() if oldViewModel?
            @unapplyViewModel(oldViewModel, @content)
            @content.html(template)
            @applyViewModel(newViewModel, @content)
            newViewModel.activate() if newViewModel?

            @viewModel = newViewModel
            @deinitViewModel(oldViewModel)

          .fail (err) =>
            log.error("Error while loading view", err)

          .always () =>
            @loading = false
            events.emit("viewmanager", "loadingview", false)
      else
        log.error("Already loading a view")

    loadDialogView: (modal, templateName, viewModelType) =>
      log.debug("Load dialog view", templateName, viewModelType)
      deferred = $.Deferred()

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
            dialogViewModel.deactivate()
            @unapplyViewModel(dialog, wrapper)
            wrapper.remove()

          # register to dialogs show event and notify view model when the view is ready and in place
          dialog.on "shown.bs.modal", () =>
            dialogViewModel.activate()

          if dialogViewModel?
            dialogViewModel.result()
              .done((res) -> deferred.resolve(res))
              .fail((err) -> deferred.reject(err))
          else
            dialog.on "hidden.bs.modal", () -> deferred.resolve()

        .fail (err) =>
          log.error("Error while creating dialog view:\n#{err.toString()}", err)
          deferred.reject(err)

      return deferred.promise()

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
      ko.applyBindings(viewModel, wrapper.get(0))

    unapplyViewModel: (viewModel, wrapper) =>
      log.debug("Unapply view model", @viewModel)
      ko.cleanNode(wrapper.get(0))

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

      return deferred.promise()

  return new ViewManagerService()
