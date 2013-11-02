define ["LoggerService"], (log) ->
  class RouterService
    init: () =>
      @registerLinkInterceptor()

    registerLinkInterceptor: () =>
      $("body").on "click", "a", @linkInterceptor

    linkInterceptor: (event) =>
      event.preventDefault()
      log.info("Intercepted link click", event)

  return new RouterService()
