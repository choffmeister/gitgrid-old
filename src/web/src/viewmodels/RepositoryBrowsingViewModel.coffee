define ["underscore", "http", "api", "ViewModelBase"], (_, http, api, ViewModelBase) ->
  class RepositoryBrowsingViewModel extends ViewModelBase
    init: (args) =>
      @args = args
      switch args.type
        when "tree" then api.get("/projects/#{args.projectId}/git/tree/master/#{args.path}").then (data) => @tree = data
        when "blob" then http.get("/api/projects/#{args.projectId}/git/blob/master/#{args.path}").then (data) => @blob = data
        else throw new Error("Unknown type '#{args.type}'")

    entryUrl: (entry) =>
      @normalize("/projects/#{@args.projectId}/git/#{entry.objectType}/master/#{@args.path}/#{entry.name}")

    normalize: (url) =>
      # ensure starting with /
      throw "Url must start with /" if not url.match(/^\//)?
      # reduce multiple slashes
      url = url.replace(/\/{2,}/g, "/")
      # remove trailing slash
      url = url.replace(/\/$/, "") if url.length >= 2
      url
