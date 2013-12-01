define ["jquery", "log", "events", "api"], ($, log, events, api) ->
  # save here to prevent direct access from the outside
  user = null

  class AuthService
    user: () => if user? then $.extend({}, user) else null
    userId: () => if user? then user.id else null

    authenticate: (userName, password) =>
      deferred = $.Deferred()

      api.post("/auth/login", { user: userName, pass: password })
        .done (res) =>
          if res.user?
            log.info("Authenticated with user name #{userName}", res)
            @changeState(res.user)
            deferred.resolve(true)
          else
            deferred.resolve(false)
        .fail (err) ->
            log.error("Error while trying to authenticate: #{err.responseText}", err)
            deferred.reject()

      return deferred.promise()

    unauthenticate: () =>
      deferred = $.Deferred()

      api.post("/auth/logout")
        .done (res) =>
          log.info("Unauthenticated")
          @changeState(null)
          deferred.resolve()
        .fail (err) ->
          log.error("Error while trying to unauthenticate: #{err.responseText}", err)
          deferred.reject()

      return deferred.promise()

    changeState: (newUser) =>
      oldUser = user

      user = newUser
      log.info("Changed authentication state", newUser)
      events.emit("auth", "changestate", newUser) if newUser?.id != oldUser?.id

    checkState: () =>
      log.info("Checking session cookie")

      deferred = $.Deferred()

      api.get("/auth/state")
        .done (res) =>
          if res.user?
            deferred.resolve(res.user)
          else
            deferred.resolve(null)
        .fail () ->
          deferred.resolve(null)

      return deferred.promise()

  return new AuthService()
