define ["jquery", "history", "log", "vm", "DashboardViewModel"], ($, history, log, vm, DashboardViewModel) ->
  class RouterService
    init: () =>
      log.info("Initializing router")
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
        history.pushState(null, null, url)

    historyInterceptor: () =>
      state = history.getState()
      log.info("Changed location to #{state.hash}", state)

      switch state.hash
        when "/" then vm.loadView("dashboard", DashboardViewModel)
        when "/test" then vm.loadView("dashboard")
        when "/test/second" then vm.loadView("foobar", DashboardViewModel)
        else
          log.warn("Unknown route #{state.hash}")

    isAbsoluteUrl: (url) ->
      absolutePatterns = [/^\w+:\/\//, /^mailto:/]

      for pattern in absolutePatterns
        if url.match(pattern)?
          return true

      return false

  return new RouterService()
