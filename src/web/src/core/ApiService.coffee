define ["config", "HttpService"], (config, http) ->
  baseUrl = config.api.baseUrl
  options =
    dataType: "json"
    accept: "application/json"
    contentType: "application/json"

  class ApiService
    get: (url) => http.get(baseUrl + url, options)
    post: (url, data) => http.post(baseUrl + url, data, options)
    put: (url, data) => http.put(baseUrl + url, data, options)
    delete: (url) => http.delete(baseUrl + url, options)

  return new ApiService()
