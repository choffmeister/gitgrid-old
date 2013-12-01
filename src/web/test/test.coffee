tests = Object.keys(window.__karma__.files).filter (file) =>
  return /^\/base\/test\/unit\/.*Spec\.js$/.test(file)

requirejs.config
  baseUrl: "/base/src"
  paths:
    # external libraries
    jquery: "../bower_components/jquery/jquery"
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
    underscore:
      exports: "_"
    history:
      exports: "History"
    bootstrap:
      deps: ["jquery"]

  deps: tests

  callback: window.__karma__.start
