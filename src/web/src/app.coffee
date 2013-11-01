requirejs.config
  baseUrl: "/src"
  paths:
    jquery: "../bower_components/jquery/jquery"
    underscore: "../bower_components/underscore/underscore"
    bootstrap: "../bower_components/bootstrap/dist/js/bootstrap"
    knockout: "../bower_components/knockout-dist/knockout"

    HttpService: "core/HttpService"
    ApiService: "core/ApiService"

  shim:
    underscore:
      exports: "_"
    bootstrap:
      deps: ["jquery"]

requirejs ["jquery", "HttpService", "ApiService"], ($, http, api) ->
  $(document).ready () ->
    http.get("/foo/bar")
      .done (result) ->
        console.log "OK", result
      .fail (error) ->
        console.log "Error", error

    api.get("foo")
      .done (result) -> console.log result
      .fail (error) -> console.log error
