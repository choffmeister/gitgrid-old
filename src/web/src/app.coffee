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
    ViewModelBase: "viewmodels/ViewModelBase"
    MainViewModel: "viewmodels/MainViewModel"
    DashboardViewModel: "viewmodels/DashboardViewModel"

  shim:
    underscore:
      exports: "_"
    bootstrap:
      deps: ["jquery"]

requirejs ["ViewManager", "DashboardViewModel"], (ViewManager, DashboardViewModel) ->
  viewManager = new ViewManager()
  viewManager.init()
  viewManager.loadView("dashboard", DashboardViewModel)
