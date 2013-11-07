requirejs.config
  baseUrl: "/src"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    history: "../bower_components/history.js/scripts/bundled/html4+html5/jquery.history"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    # configuration
    config: "config"

    # services
    log: "core/LoggerService"
    events: "core/EventService"
    http: "core/HttpService"
    api: "core/ApiService"
    router: "core/RouterService"
    vm: "core/ViewManagerService"

    # misc
    SlideVisibleBinding: "utils/SlideVisibleBinding"
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

requirejs [
  "jquery"
  "knockout"
  "log"
  "vm"
  "router"
  "SlideVisibleBinding"
  "DashboardViewModel"
], ($, ko, log, vm, router, SlideVisibleBinding, DashboardViewModel) ->
  $(document).ready () ->
    log.info("Initializing view manager")
    vm.init()
    vm.loadView("dashboard", DashboardViewModel)

    log.info("Initializing router")
    router.init()
