define ["jquery", "bootstrap", "knockout", "log", "events", "cache", "http", "ViewModelBase", "viewmodels/MainViewModel"], ($, bs, ko, log, events, cache, http, ViewModelBase, MainViewModel) ->
  class ViewState
    constructor: (@vms, @templateName, @viewModelType, @parameters) ->

    init: () =>
      log.debug("Init view state", @templateName)
      deferred = $.Deferred()
      @viewModel = if @viewModelType? then new @viewModelType() else new ViewModelBase()

      $.when(@vms.loadTemplate(@templateName), @vms.initViewModel(@viewModel, @parameters))
        .done (template) =>
            try
              @dom = @createDom(template)
              ko.applyBindings(@viewModel, @dom.get(0))
              deferred.resolve()
            catch ex
              @deinit()
              deferred.reject(ex)
        .fail (error) =>
          deferred.reject(error)

      deferred.promise()

    deinit: () =>
      log.debug("Deinit view state", this)
      ko.cleanNode(@dom.get(0))
      @destroyDom(@dom)
      @vms.deinitViewModel(@viewModel)

    activate: () =>
      log.debug("Activate view state", this)
      @viewModel.activate() if @viewModel?
      $("#content").append(@dom)

    deactivate: () =>
      log.debug("Deactivate view state", this)
      $("#shadow").append(@dom)
      @viewModel.deactivate() if @viewModel?

    createDom: (template) =>
      log.debug("Create DOM for view state", this)
      dom = $("<div>#{template}</div>")
      $("#shadow").append(dom)
      dom

    destroyDom: (dom) =>
      log.debug("Destroy DOM for view state", this)
      $(dom).remove()

  class DialogViewState extends ViewState
    activate: () =>
      @viewModel.result()
      log.debug("Activate view state", this)
      @dom.modal({ backdrop: true })

    deactivate: () =>
      @dom.modal("hide")

    createDom: (template) =>
      log.debug("Create DOM for view state", this)
      dom = $(template)

      dom.on "hidden.bs.modal", () =>
        @deactivate()
        @deinit()
      dom.on "shown.bs.modal", () =>
        @viewModel.activate() if @viewModel?
      if @viewModel?
        @viewModel.result().always () =>
          dom.modal("hide")

      $("#dialogs").append(dom)
      dom

  class ViewManagerService
    constructor: () ->
      @state = null
      @loading = false

    init: () =>
      log.info("Initializing view manager")
      @body = $("body")
      @mainViewModel = new MainViewModel()
      @mainViewModel.init(this)
      ko.applyBindings(@mainViewModel, @body.get(0))

    switchView: (newState) =>
      if @state?
        @state.deactivate()
        @state.deinit()
        @state = null
      newState.activate()
      @state = newState

    loadView: (templateName, viewModelType, parameters) =>
      deferred = $.Deferred()
      newState = new ViewState(this, templateName, viewModelType, parameters)
      newState.init()
        .done () =>
          @switchView(newState)
          deferred.resolve(newState)
        .fail (err) =>
          log.error("Error while loading view: #{@errorMessage(err)}", err)
          events.emit("notification", "error", { title: "Error", message: "Error while loading view: #{@errorMessage(err)}" })
          deferred.reject(err)
      deferred.promise()

    loadDialogView: (modal, templateName, viewModelType, parameters) =>
      deferred = $.Deferred()
      dialogState = new DialogViewState(this, templateName, viewModelType, parameters)
      dialogState.init()
        .done () =>
          dialogState.activate()
          deferred.resolve(dialogState)
        .fail (err) =>
          log.error("Error while loading dialog view: #{@errorMessage(err)}", err)
          events.emit("notification", "error", { title: "Error", message: "Error while loading dialog view: #{@errorMessage(err)}" })
          deferred.reject(err)
      deferred.promise()

    initViewModel: (viewModel, parameters) =>
      log.debug("Init view model", viewModel)
      if viewModel?
        try
          viewModel.init(parameters)
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

    errorMessage: (error) ->
      error.toString()

    loadTemplate: (templateName) ->
      log.debug("Load template", "templateName")
      cache.get("templates:#{templateName}", () -> http.get("/views/#{templateName}.html"))

  return new ViewManagerService()
