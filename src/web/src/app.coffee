requirejs.config
  baseUrl: "/src"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    history: "../bower_components/history.js/scripts/bundled-uncompressed/html4+html5/native.history"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    # configuration
    config: "config"

    # services
    log: "core/LoggerService"
    events: "core/EventService"
    http: "core/HttpService"
    api: "core/ApiService"
    auth: "core/AuthService"
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
  "auth"
  "vm"
  "router"
  "SlideVisibleBinding"
  "DashboardViewModel"
], ($, ko, log, auth, vm, router, SlideVisibleBinding, DashboardViewModel) ->
  $(document).ready () ->
    $.when(auth.checkState(), vm.init(), router.init())
      .done (user) ->
        log.info("Application initialization done")
        auth.changeState(user)
      .fail (err) ->
        log.fatal("Error while trying to initialize the application", err)
        window.alert("Error while trying to initialize the application")
      .always () ->
        router.addRoute("/", "dashboard", DashboardViewModel)
        router.addRoute("/about", "about")

        router.historyInterceptor()
        $(".cloak").removeClass("cloak")
