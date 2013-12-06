define ["jquery", "history", "log", "events", "vm"], ($, history, log, events, vm) ->
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
      @historyCount = 0
      @registerLinkInterceptor()
      @registerHistoryInterceptor()

    registerLinkInterceptor: () =>
      $("body").on "click", "a", @linkInterceptor

    registerHistoryInterceptor: () =>
      history.Adapter.bind window, "statechange", @historyInterceptor

    linkInterceptor: (event) =>
      url = $(event.currentTarget).attr("href")

      if url != "#" and not @isAbsoluteUrl(url)
        event.preventDefault()
        log.trace("Intercepted link click to #{url}", event)
        @redirect(url)

    historyInterceptor: () =>
      state = history.getState()
      # fix issue in IE9
      # TODO find proper solution (this solutions does not solve the problem of ugly urls prefixed with a dot)
      url = state.hash.match(/^\.?(.*)$/)[1]
      log.info("Changed location to #{url}", state)

      matchingRoute = @matchRoute(url)
      promise = null
      if matchingRoute?
        promise = vm.loadView(matchingRoute.templateName, matchingRoute.viewModelType, matchingRoute.parameters)
      else
        log.warn("Unknown route #{state.hash}")
        events.emit("notification", "warning", { title: "Unknown route", message: "The route '#{state.hash}' is unknown" })
        deferred = $.Deferred()
        deferred.reject()
        promise = deferred.promise()

      promise.fail (err) =>
        if @historyCount > 0
          @historyCount -= 1
          history.back()
        else
          @historyCount = 1
          history.pushState(null, null, "/")

    redirect: (url) =>
      @historyCount += 1
      history.pushState(null, null, url)

    addRoute: (pattern, templateName, viewModelType, defaultParameters) =>
      route = @createRoute(pattern)
      @routes.push({
        route: route,
        templateName: templateName,
        viewModelType: viewModelType,
        defaultParameters: defaultParameters
      })

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

    createRoute: (pattern) => new Route(pattern)

    isAbsoluteUrl: (url) ->
      absolutePatterns = [/^\w+:\/\//, /^mailto:/]

      for pattern in absolutePatterns
        if url.match(pattern)?
          return true

      return false

  return new RouterService()
