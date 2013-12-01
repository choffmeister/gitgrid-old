define ["config", "events", "http"], (config, events, http) ->
  baseUrl = config.api.baseUrl
  options =
    dataType: "json"
    contentType: "application/json"

  serialize = (data) -> JSON.stringify(data)

  fail = (err) ->
    switch err.status
      when 401 then events.emit("notification", "warning", { title: "Unauthorized", message: "You are either not logged in or have insufficient permissions" })
      when 404 then events.emit("notification", "error", { title: "Not found", message: "The resource you requested could not be found at the server" })
      when 500 then events.emit("notification", "error", { title: "Internal server error", message: "The server had an internal error" })
      else events.emit("notification", "error", { title: "#{err.status} #{err.statusText}", message: err.responseText or err.statusText or "Unknown error" })

  class ApiService
    get: (url) => http.get(baseUrl + url, options).fail(fail)
    post: (url, data) => http.post(baseUrl + url, serialize(data), options).fail(fail)
    put: (url, data) => http.put(baseUrl + url, serialize(data), options).fail(fail)
    delete: (url) =>  http.delete(baseUrl + url, options).fail(fail)

  return new ApiService()
