requirejs.config
  baseUrl: "/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    config: "config"

    LoggerService: "core/LoggerService"
    EventService: "core/EventService"
    HttpService: "core/HttpService"
    ApiService: "core/ApiService"
    ViewManagerService: "core/ViewManagerService"
    RouterService: "core/RouterService"
    ViewModelBase: "viewmodels/ViewModelBase"
    DialogViewModelBase: "viewmodels/DialogViewModelBase"
    MainViewModel: "viewmodels/MainViewModel"
    LoginDialogViewModel: "viewmodels/LoginDialogViewModel"
    DashboardViewModel: "viewmodels/DashboardViewModel"

  shim:
    underscore:
      exports: "_"
    bootstrap:
      deps: ["jquery"]

requirejs ["LoggerService", "ViewManagerService", "RouterService", "DashboardViewModel"], (log, viewManager, router, DashboardViewModel) ->
  log.info("Initializing view manager")
  viewManager.init()
  viewManager.loadView("dashboard", DashboardViewModel)

  log.info("Initializing router")
  router.init()
