requirejs.config
  baseUrl: "/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    HttpService: "core/HttpService"
    ApiService: "core/ApiService"
    ViewManager: "core/ViewManager"
    ViewModelBase: "viewmodels/ViewModelBase"
    TestViewModel: "viewmodels/TestViewModel"

  shim:
    underscore:
      exports: "_"
    bootstrap:
      deps: ["jquery"]

requirejs ["ViewManager", "TestViewModel"], (ViewManager, TestViewModel) ->
  viewManager = new ViewManager()
  viewManager.init()
  viewManager.loadView("test", TestViewModel)
