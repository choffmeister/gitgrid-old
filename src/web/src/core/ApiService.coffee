define ["config", "events", "http"], (config, events, http) ->
  baseUrl = config.api.baseUrl
  options =
    dataType: "json"
    contentType: "application/json"

  serialize = (data) -> JSON.stringify(data)

  fail = (err) -> events.emit("notification", "error", { title: "#{err.status} #{err.statusText}", message: err.responseText }) if err.status != 401

  class ApiService
    get: (url) => http.get(baseUrl + url, options).fail(fail)
    post: (url, data) => http.post(baseUrl + url, serialize(data), options).fail(fail)
    put: (url, data) => http.put(baseUrl + url, serialize(data), options).fail(fail)
    delete: (url) =>  http.delete(baseUrl + url, options).fail(fail)

  return new ApiService()
