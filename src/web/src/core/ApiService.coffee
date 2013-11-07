define ["config", "http"], (config, http) ->
  baseUrl = config.api.baseUrl
  options =
    dataType: "json"
    contentType: "application/json"

  serialize = (data) -> JSON.stringify(data)

  class ApiService
    get: (url) => http.get(baseUrl + url, options)
    post: (url, data) => http.post(baseUrl + url, serialize(data), options)
    put: (url, data) => http.put(baseUrl + url, serialize(data), options)
    delete: (url) => http.delete(baseUrl + url, options)

  return new ApiService()
