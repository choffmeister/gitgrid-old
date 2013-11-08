define ["jquery", "history", "log", "vm", "DashboardViewModel"], ($, history, log, vm, DashboardViewModel) ->
  class RouterService
    init: () =>
      log.info("Initializing router")
      @historyCount = 0
      @registerLinkInterceptor()
      @registerHistoryInterceptor()

    registerLinkInterceptor: () =>
      $("body").on "click", "a", @linkInterceptor

    registerHistoryInterceptor: () =>
      history.Adapter.bind window, "statechange", @historyInterceptor

    linkInterceptor: (event) =>
      url = $(event.currentTarget).attr("href")

      if not @isAbsoluteUrl(url)
        event.preventDefault()
        log.trace("Intercepted link click to #{url}", event)
        @historyCount += 1
        history.pushState(null, null, url)

    historyInterceptor: () =>
      state = history.getState()
      log.info("Changed location to #{state.hash}", state)

      promise = switch state.hash
        when "/" then vm.loadView("dashboard", DashboardViewModel)
        when "/test" then vm.loadView("dashboard")
        when "/test/second" then vm.loadView("foobar", DashboardViewModel)
        else
          log.warn("Unknown route #{state.hash}")
          deferred = $.Deferred()
          deferred.reject()
          deferred.promise()

      promise.fail (err) =>
        if @historyCount > 0
          @historyCount -= 1
          history.back()
        else
          @historyCount = 1
          history.pushState(null, null, "/")

    isAbsoluteUrl: (url) ->
      absolutePatterns = [/^\w+:\/\//, /^mailto:/]

      for pattern in absolutePatterns
        if url.match(pattern)?
          return true

      return false

  return new RouterService()
