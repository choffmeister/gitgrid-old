define ["jquery", "log", "events", "vm"], ($, log, events, vm) ->
  class Route
    constructor: (pattern) ->
      @compile(pattern)

    match: (url) =>
      matchings = url.match(@regex)
      if matchings?
        values = {}

        for matching, i in matchings[1..]
          values[@parameters[i]] = matching

        return values
      else
        return null

    compile: (pattern) =>
      # ensure starting with /
      throw "Pattern must start with /" if not pattern.match(/^\//)?
      # reduce multiple slashes
      pattern = pattern.replace(/\/{2,}/g, "/")
      # remove trailing slash
      pattern = pattern.replace(/\/$/, "") if pattern.length >= 2

      if pattern == "/"
        @pattern = "/"
        @parameters = []
        @regex = new RegExp("^/$")
      else
        @hasCatchAll = false
        @pattern = pattern.replace("/$", "")
        @parameters = []

        patternCallback = (match, name) =>
          @parameters.push(name)
          return "([^/]+)"
        patternCallback2 = (match, name) =>
          @hasCatchAll = true
          @parameters.push(name)
          return "(.*)"

        regex = pattern
          .replace(/\{([a-zA-Z0-9\-\_]+)\}/g, patternCallback)
          .replace(/\{\*([a-zA-Z0-9\-\_]+)\}/g, patternCallback2)
        if @hasCatchAll
          regex = "^#{regex}$"
        else
          regex = "^#{regex}/?$"
        @regex = new RegExp(regex)

  class RouterService
    init: () =>
      log.info("Initializing router")
      @routes = []
      window.onhashchange = () => @handle()

    handle: () =>
      deferred = $.Deferred()
      url = @normalize(window.location.hash)
      route = @matchRoute(url)
      if route?
        vm.loadView(route.templateName, route.viewModelType, route.parameters)
          .done (state) =>
            deferred.resolve()
          .fail (err) =>
            deferred.reject(err)
      else
        events.emit("notification", "warning", { title: "Unknown route", message: "The route '#{url}' is unknown" })
        deferred.reject("Unknown route '#{url}'")
      deferred.promise()

    createRoute: (pattern) => new Route(pattern)

    addRoute: (pattern, templateName, viewModelType, defaultParameters) =>
      route = @createRoute(pattern)
      @routes.push({
        route: route,
        templateName: templateName,
        viewModelType: viewModelType,
        defaultParameters: defaultParameters
      })

    addRoutes: (routes) =>
      for route in routes
        @addRoute(route[0], route[1], route[2], route[3])

    matchRoute: (url) =>
      for route in @routes
        matching = route.route.match(url)
        if matching?
          return {
            templateName: route.templateName,
            viewModelType: route.viewModelType,
            parameters: $.extend({}, matching, route.defaultParameters or {})
          }
      return null

    navigate: (url) =>
      window.location.hash = "#!" + @normalize(url)

    redirect: (url) => navigate(url)

    normalize: (url) ->
      match = url.match(/^\/?#?!?\/?(.*)$/)
      return "/" + match[1].replace(/\/{2,}/g, "/")

  return new RouterService()
