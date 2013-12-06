define ["jquery", "underscore", "http", "api", "router", "ViewModelBase"], ($, _, http, api, router, ViewModelBase) ->
  normalizePath = (url) =>
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

  class GitBrowserViewModel extends ViewModelBase
    init: (args) =>
      @projectId = args.projectId
      @objectType = args.type
      @refOrSha = args.refOrSha
      @path = normalizePath(args.path)
      @pathParts = @splitPath(args.path)

      req1 = switch @objectType
        when "tree" then api.get("/projects/#{@projectId}/git/tree/#{@refOrSha}#{@path}").then (data) => @tree = data
        when "blob" then http.get("/api/projects/#{@projectId}/git/blob/#{@refOrSha}#{@path}").then (data) => @blob = data
        else throw new Error("Unknown type '#{@objectType}'")
      req2 = api.get("/projects/#{@projectId}/git/branches").then (data) => @branches = data
      req3 = api.get("/projects/#{@projectId}/git/tags").then (data) => @tags = data
      $.when(req1, req2, req3)

    changeToSha: (sha) =>
      router.redirect(@url(@objectType, sha, @path))

    changeToBranch: (branch) =>
      match = branch.match(/^refs\/heads\/(.*)$/)
      if match?
        router.redirect(@url(@objectType, match[1], @path))
      else
        @notifyError("Branch '#{branch}' is not a valid branch name")

    changeToTag: (tag) =>
      match = tag.match(/^refs\/tags\/(.*)$/)
      if match?
        router.redirect(@url(@objectType, match[1], @path))
      else
        @notifyError("Tag '#{tag}' is not a valid tag name")

    entryUrl: (entry) =>
      @url(entry.objectType, @refOrSha, "#{@path}/#{entry.name}")

    url: (objectType, refOrSha, path) =>
      res = normalizePath("/projects/#{@projectId}/#{objectType}/#{refOrSha}#{path}")
      res = res + "/" if not path? or path == "" or path == "/"
      res

    splitPath: (path) =>
      parts = normalizePath(path).split("/")
      currentPath = ""
      for i in [0..parts.length - 1]
        currentName = if i > 0 then parts[i] else "[root]"
        currentType = if i < parts.length - 1 then "tree" else "blob"
        currentPath = "#{currentPath}/#{parts[i]}"
        currentUrl = if i > 0 then @url(currentType, @refOrSha, currentPath) else @url(currentType, @refOrSha, currentPath) + "/"
        { name: currentName, url: currentUrl }
