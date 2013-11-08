define ["jquery", "log", "events", "api"], ($, log, events, api) ->
  class AuthService
    constructor: () ->
      @user = null

    authenticate: (userName, password) =>
      deferred = $.Deferred()

      api.post("/auth/login", { user: userName, pass: password })
        .done (res) ->
          log.info("Authenticated with user name #{userName}", res)
          @user = res.user
          events.emit("auth", "changestate", @user)
          deferred.resolve(true)
        .fail (err) ->
          switch err.status
            when 401 then deferred.resolve(false)
            else
              log.error("Error while trying to authenticate: #{err.responseText}", err)
              deferred.reject()

      return deferred.promise()

    unauthenticate: () =>
      deferred = $.Deferred()

      api.post("/auth/logout")
        .done (res) ->
          log.info("Unauthenticated")
          @user = null
          events.emit("auth", "changestate", @user)
          deferred.resolve()
        .fail (err) ->
          log.error("Error while trying to unauthenticate: #{err.responseText}", err)
          deferred.reject()

      return deferred.promise()

  return new AuthService()
