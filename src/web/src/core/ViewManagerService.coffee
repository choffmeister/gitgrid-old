define ["jquery", "bootstrap", "knockout", "log", "events", "http", "MainViewModel"], ($, bs, ko, log, events, http, MainViewModel) ->
  class ViewManagerService
    constructor: () ->
      @templateCache = {}
      @viewModel = null
      @loading = false

    init: () =>
      log.info("Initializing view manager")
      @body = $("body")
      @content = $("#content")
      @dialogs = $("#dialogs")
      @mainViewModel = new MainViewModel()
      @mainViewModel.init(this)
      ko.applyBindings(@mainViewModel, @body.get(0))

    loadView: (templateName, viewModelType) =>
      deferred = $.Deferred()

      if @loading is false
        log.debug("Load view", templateName, viewModelType)
        @loading = true
        events.emit("viewmanager", "loadingview", true)

        # instantiate view model if type was specified
        oldViewModel = @viewModel
        newViewModel = if viewModelType? then new viewModelType() else null

        $.when(@loadTemplate(templateName), @initViewModel(newViewModel))
          .done (template) =>
            try
              # swap views
              oldViewModel.deactivate() if oldViewModel?
              @unapplyViewModel(oldViewModel, @content)
              @content.html(template)
              @applyViewModel(newViewModel, @content)
              newViewModel.activate() if newViewModel?

              @viewModel = newViewModel
              @deinitViewModel(oldViewModel)
              deferred.resolve()
            catch ex
              log.error("Error while applying view", ex)
              @loadNotification(true, "Error while loading view")
              deferred.reject(ex)

          .fail (err) =>
            log.error("Error while loading view", err)
            @loadNotification(true, "Error while loading view")
            deferred.reject(err)

          .always () =>
            @loading = false
            events.emit("viewmanager", "loadingview", false)
      else
        log.error("Already loading a view")
        @loadNotification(true, "Already loading a view")
        deferred.reject()

      return deferred.promise()

    loadDialogView: (modal, templateName, viewModelType) =>
      log.debug("Load dialog view", templateName, viewModelType)
      deferred = $.Deferred()

      # instantiate view model if type was specified
      dialogViewModel = if viewModelType? then new viewModelType() else null

      # load template and initialize view model
      $.when(@loadTemplate(templateName), @initViewModel(dialogViewModel))
        .done (template) =>
          @openDialogView(modal, template, dialogViewModel)
            .done (res) -> deferred.resolve(res)
            .fail (err) -> deferred.reject(err)

        .fail (err) =>
          log.error("Error while creating dialog view:\n#{err.toString()}", err)
          @loadNotification(true, "Error while creating dialog view")
          deferred.reject(err)

      return deferred.promise()

    loadNotification: (backdrop, text) =>
      deferred = $.Deferred()

      @loadTemplate("notificationdialog")
        .done (templateRaw) =>
          template = templateRaw.replace("{{text}}", text)
          return @openDialogView(backdrop, template, null)

        .fail (err) ->
          deferred.reject(err)

      return deferred.promise()

    openDialogView: (backdrop, template, viewModel) =>
      deferred = $.Deferred()

      try
        # wrap dialog template and append to DOM
        dialog = $(template)
        wrapper = $("<div></div>").append(dialog)
        @dialogs.append(wrapper)

        # apply view model
        @applyViewModel(viewModel, dialog)

        # show dialog
        dialog.modal({ backdrop: backdrop })

        # register to view models result promise and hide dialog if promise is resolved or rejected
        if viewModel?
          viewModel.result().always () =>
            dialog.modal("hide")

        # register to dialogs hide event and remove dialog from DOM after closing
        dialog.on "hidden.bs.modal", () =>
          viewModel.deactivate() if viewModel?
          @unapplyViewModel(dialog, wrapper)
          wrapper.remove()

        # register to dialogs show event and notify view model when the view is ready and in place
        dialog.on "shown.bs.modal", () =>
          viewModel.activate() if viewModel?

        if viewModel?
          viewModel.result()
            .done((res) -> deferred.resolve(res))
            .fail((err) -> deferred.reject(err))
        else
          dialog.on "hidden.bs.modal", () -> deferred.resolve()
      catch ex
        log.error("Error while opening dialog view", ex)
        deferred.reject(ex)

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
