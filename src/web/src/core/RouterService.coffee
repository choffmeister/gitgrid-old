define ["jquery", "history", "LoggerService", "ViewManagerService", "DashboardViewModel"], ($, history, log, vm, DashboardViewModel) ->
  class RouterService
    init: () =>
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
        log.info("Intercepted link click to #{url}", event)
        history.pushState(null, null, url)
        vm.loadView("dashboard", DashboardViewModel) if url is "/"

    historyInterceptor: () =>
      state = history.getState()
      log.info("Changed location", state)

    isAbsoluteUrl: (url) ->
      absolutePatterns = [/^\w+:\/\//, /^mailto:/]

      for pattern in absolutePatterns
        if url.match(pattern)?
          return true

      return false

  return new RouterService()
