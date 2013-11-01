tests = Object.keys(window.__karma__.files).filter (file) =>
  return /^\/base\/test\/unit\/.*Spec\.js$/.test(file)

requirejs.config
  baseUrl: "/base/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

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

  deps: tests

  callback: window.__karma__.start
