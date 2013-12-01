requirejs.config
  baseUrl: "/src"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
    jquerytransit: "../bower_components/jquery.transit/jquery.transit"
    underscore: "../bower_components/underscore/underscore"
    history: "../bower_components/history.js/scripts/bundled-uncompressed/html4+html5/native.history"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"
    knockoutvalidation: "../bower_components/knockout.validation/Dist/knockout.validation"
    knockoutmapping: "../bower_components/knockout-mapping/build/output/knockout.mapping-latest.debug"

    # configuration
    config: "config"
    routes: "routes"

    # services
    log: "core/LoggerService"
    events: "core/EventService"
    http: "core/HttpService"
    api: "core/ApiService"
    auth: "core/AuthService"
    router: "core/RouterService"
    vm: "core/ViewManagerService"

    # base viewmodels
    ViewModelBase: "viewmodels/ViewModelBase"
    DialogViewModelBase: "viewmodels/DialogViewModelBase"

    # base model
    ModelBase: "models/ModelBase"

  shim:
    jquerytransit:
      deps: ["jquery"]
    underscore:
      exports: "_"
    history:
      exports: "History"
    bootstrap:
      deps: ["jquery"]

requirejs [
  "jquery"
  "jquerytransit"
  "knockout"
  "knockoutvalidation"
  "knockoutmapping"
  "log"
  "auth"
  "vm"
  "router"
  "routes"
  "utils/SlideVisibleBinding"
], ($, $transit, ko, koval, komap, log, auth, vm, router, routes, SlideVisibleBinding) ->
  ko.validation.init
    insertMessages: false
    decorateElement: true
    errorElementClass: "has-error"
    grouping:
      deep: true
  ko.mapping = komap

  # bootstrap application
  $(document).ready () ->
    $.when(auth.checkState(), vm.init(), router.init())
      .done (user) ->
        log.info("Application initialization done")
        auth.changeState(user)
        for route in routes
          router.addRoute(route[0], route[1], route[2])
        router.historyInterceptor()
        $(".cloak").removeClass("cloak")
      .fail (err) ->
        log.fatal("Error while trying to initialize the application", err)
        window.alert("Error while trying to initialize the application")
