tests = Object.keys(window.__karma__.files).filter (file) =>
  return /^\/base\/js\/test\/unit\/.*Spec\.js$/.test(file)

requirejs.config
  baseUrl: "/base/js"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
    jquerytransit: "../bower_components/jquery.transit/jquery.transit"
    underscore: "../bower_components/underscore/underscore"
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
    bootstrap:
      deps: ["jquery"]

  deps: tests

  callback: window.__karma__.start
