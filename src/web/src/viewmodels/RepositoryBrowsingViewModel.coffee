define ["underscore", "http", "api", "ViewModelBase"], (_, http, api, ViewModelBase) ->
  class RepositoryBrowsingViewModel extends ViewModelBase
    init: (args) =>
      @projectId = args.projectId
      @path = RepositoryBrowsingViewModel.normalizePath(args.path)
      @pathParts = @splitPath(args.path)
      console.log(@pathParts)

      switch args.type
        when "tree" then api.get("/projects/#{args.projectId}/git/tree/master#{@path}").then (data) => @tree = data
        when "blob" then http.get("/api/projects/#{args.projectId}/git/blob/master#{@path}").then (data) => @blob = data
        else throw new Error("Unknown type '#{args.type}'")

    entryUrl: (entry) =>
      @url(entry.objectType, "#{@path}/#{entry.name}")

    url: (objectType, path) =>
      RepositoryBrowsingViewModel.normalizePath("/projects/#{@projectId}/#{objectType}/master#{path}")

    splitPath: (path) =>
      parts = RepositoryBrowsingViewModel.normalizePath(path).split("/")
      currentPath = ""
      for i in [0..parts.length - 1]
        currentName = if i > 0 then parts[i] else "[root]"
        currentType = if i < parts.length - 1 then "tree" else "blob"
        currentPath = "#{currentPath}/#{parts[i]}"
        currentUrl = if i > 0 then @url(currentType, currentPath) else @url(currentType, currentPath) + "/"
        { name: currentName, url: currentUrl }

    @normalizePath: (url) =>
      # ensure starting with /
      url = if url.length > 0
        if url[0] == "/" then url else "/" + url
      else
        "/"
      # reduce multiple slashes
      url = url.replace(/\/{2,}/g, "/")
      # remove trailing slash
      url = url.replace(/\/$/, "") if url.length >= 2
      url
