requirejs.config
  baseUrl: "/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    config: "config"

    Logger: "core/Logger"
    EventService: "core/EventService"
    HttpService: "core/HttpService"
    ApiService: "core/ApiService"
    ViewManager: "core/ViewManager"
    Router: "core/Router"
    ViewModelBase: "viewmodels/ViewModelBase"
    MainViewModel: "viewmodels/MainViewModel"
    DashboardViewModel: "viewmodels/DashboardViewModel"

  shim:
    underscore:
      exports: "_"
    bootstrap:
      deps: ["jquery"]

requirejs ["Logger", "ViewManager", "Router", "DashboardViewModel"], (log, ViewManager, Router, DashboardViewModel) ->
  log.info("Initializing view manager")
  viewManager = new ViewManager()
  viewManager.init()
  viewManager.loadView("dashboard", DashboardViewModel)

  log.info("Initializing router")
  router = new Router()
  router.init()
