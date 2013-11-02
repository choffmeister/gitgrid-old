define ["Logger"], (log) ->
  class Router
    init: () =>
      @registerLinkInterceptor()

    registerLinkInterceptor: () =>
      $("body").on "click", "a", @linkInterceptor

    linkInterceptor: (event) =>
      event.preventDefault()
      log.info("Intercepted link click", event)
