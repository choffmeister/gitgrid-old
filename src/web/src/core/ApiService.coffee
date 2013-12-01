define ["config", "http", "vm"], (config, http, vm) ->
  baseUrl = config.api.baseUrl
  options =
    dataType: "json"
    contentType: "application/json"

  serialize = (data) -> JSON.stringify(data)

  handle = (err) -> vm.showNotificationError("<strong>#{err.status} #{err.statusText}:</strong> #{err.responseText}") if err.status != 401

  class ApiService
    get: (url) => http.get(baseUrl + url, options).fail(handle)
    post: (url, data) => http.post(baseUrl + url, serialize(data), options).fail(handle)
    put: (url, data) => http.put(baseUrl + url, serialize(data), options).fail(handle)
    delete: (url) =>  http.delete(baseUrl + url, options).fail(handle)

  return new ApiService()
