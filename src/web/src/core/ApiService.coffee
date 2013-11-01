define ["HttpService"], (http) ->
  class ApiService
    constructor: () ->
      @baseUrl = "/ap/"
      @options =
        dataType: "json"
        accept: "application/json"
        contentType: "application/json"

    get: (url) => http.get(@baseUrl + url, @options)
    post: (url, data) => http.post(@baseUrl + url, data, @options)
    put: (url, data) => http.put(@baseUrl + url, data, @options)
    delete: (url) => http.delete(@baseUrl + url, @options)

  return new ApiService()
