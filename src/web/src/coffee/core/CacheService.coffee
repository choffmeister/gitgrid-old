define ["jquery"], ($) ->
  class CacheService
    constructor: () ->
      @cache = {}

    # gets a named value if already existent, or else
    # executes the value callback to retrieve the value,
    # then adds it to the cache and returns the value
    get: (name, valueCallback) ->
      deferred = $.Deferred()

      cachedValue = @cache[name]
      if not cachedValue?
        valueCallback()
          .done (value) =>
            @cache[name] = value
            deferred.resolve(value)
          .fail (error) =>
            deferred.reject(error)
      else
        deferred.resolve(cachedValue)

      return deferred.promise()

  return new CacheService
