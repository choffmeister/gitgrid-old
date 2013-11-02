requirejs.config
  baseUrl: "/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    history: "../bower_components/history.js/scripts/bundled/html4+html5/jquery.history"
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
    history:
      exports: "History"
    bootstrap:
      deps: ["jquery"]

requirejs ["jquery", "LoggerService", "ViewManagerService", "RouterService", "DashboardViewModel"], ($, log, viewManager, router, DashboardViewModel) ->
  $(document).ready () ->
    log.info("Initializing view manager")
    viewManager.init()
    viewManager.loadView("dashboard", DashboardViewModel)

    log.info("Initializing router")
    router.init()
