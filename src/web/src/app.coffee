requirejs.config
  baseUrl: "/src"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
    jquerytransit: "../bower_components/jquery.transit/jquery.transit"
    underscore: "../bower_components/underscore/underscore"
    history: "../bower_components/history.js/scripts/bundled-uncompressed/html4+html5/native.history"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    bootstrapdatepicker: "../bower_components/bootstrap-datepicker/js/bootstrap-datepicker"
    knockout: "../bower_components/knockout-dist/knockout"
    knockoutvalidation: "../bower_components/knockout.validation/Dist/knockout.validation"
    knockoutmapping: "../bower_components/knockout-mapping/build/output/knockout.mapping-latest.debug"
    microplugin: "../bower_components/microplugin/src/microplugin"
    sifter: "../bower_components/sifter/sifter"
    selectize: "../bower_components/selectize/dist/js/selectize"

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
    bootstrapdatepicker:
      deps: ["bootstrap"]

requirejs [
  "jquery"
  "jquerytransit"
  "knockout"
  "knockoutvalidation"
  "knockoutmapping"
  "bootstrap"
  "bootstrapdatepicker"
  "selectize"
  "log"
  "auth"
  "vm"
  "router"
  "routes"
  "utils/SlideVisibleBinding"
  "utils/DateValueBinding"
  "utils/SelectizeBindings"
], ($, $transit, ko, koval, komap, bs, bsdatepicker, selectize, log, auth, vm, router, routes) ->
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
          router.addRoute(route[0], route[1], route[2], route[3])
        router.historyInterceptor()
        $(".cloak").removeClass("cloak")
      .fail (err) ->
        log.fatal("Error while trying to initialize the application", err)
        window.alert("Error while trying to initialize the application")
