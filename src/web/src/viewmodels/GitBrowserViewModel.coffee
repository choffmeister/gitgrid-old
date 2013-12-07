define ["jquery", "underscore", "http", "api", "router", "ViewModelBase"], ($, _, http, api, router, ViewModelBase) ->
  branchRegex = /^refs\/heads\/(.*)$/
  tagRegex = /^refs\/tags\/(.*)$/
  shaRegex = /^[0-9a-f]{40}$/

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
      @refs = []
      @ref = @observable()

      req1 = switch @objectType
        when "tree" then api.get("/projects/#{@projectId}/git/tree/#{@refOrSha}#{@path}").then (data) => @tree = data
        when "blob" then http.get("/api/projects/#{@projectId}/git/blob/#{@refOrSha}#{@path}").then (data) => @blob = data
        else throw new Error("Unknown type '#{@objectType}'")
      req2 = api.get("/projects/#{@projectId}/git/branches").then (data) => _.each(data, (r) => @refs.push(r))
      req3 = api.get("/projects/#{@projectId}/git/tags").then (data) =>  _.each(data, (r) => @refs.push(r))
      $.when(req1, req2, req3).then () =>
        @refs.sort (left, right) ->
          if left.name < right.name then -1
          else if left.name > right.name then 1
          else 0
        currentRef = _.find @refs, (r) => r.id == @refOrSha or r.name == "refs/heads/#{@refOrSha}" or r.name == "refs/tags/#{@refOrSha}"
        if currentRef?
          @ref(currentRef.name)
        @ref.subscribe (newRef) => @change(newRef)

    change: (refOrSha) =>
      if (match = refOrSha.match(branchRegex))?
        router.redirect(@url(@objectType, match[1], @path))
      else if (match = refOrSha.match(tagRegex))?
        router.redirect(@url(@objectType, match[1], @path))
      else if (match = refOrSha.match(shaRegex))?
        router.redirect(@url(@objectType, sha, @path))
      else
        @notifyError("Cannot parse reference or SHA '#{refOrSha}'")

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
