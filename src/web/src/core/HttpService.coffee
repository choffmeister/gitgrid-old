define ["jquery"], ($) ->
  class HttpService
    get: (url, options) -> @request("GET", url, null, options)
    post: (url, data, options) -> @request("POST", url, data, options)
    put: (url, data, options) -> @request("PUT", url, data, options)
    delete: (url, options) -> @request("DELETE", url, null, options)
    request: (method, url, data, options) ->
      deferred = $.Deferred();
      options = {} if not options?

      settings = $.extend(options, {
        url: url,
        type: method,
        data: data
      })

      $.ajax(settings)
        .done((result) -> deferred.resolve(result))
        .fail((error) -> deferred.reject(error))

      deferred.promise()

  return new HttpService()
