define ["jquery", "../../src/core/RouterService.js"], ($, router) ->
  describe "Route", () ->
    it "compile routes", () ->
      r1 = router.createRoute("/")
      expect(r1.regex).toEqual(new RegExp("^/$"))
      expect(r1.parameters).toEqual([])

      r2 = router.createRoute("/{name}")
      expect(r2.regex).toEqual(new RegExp("^/([^/]+)/?$"))
      expect(r2.parameters).toEqual(["name"])

      r2a = router.createRoute("/{name}/")
      expect(r2a.regex).toEqual(new RegExp("^/([^/]+)/?$"))
      expect(r2a.parameters).toEqual(["name"])

      r3 = router.createRoute("/search/{name}/{age}")
      expect(r3.regex).toEqual(new RegExp("^/search/([^/]+)/([^/]+)/?$"))
      expect(r3.parameters).toEqual(["name", "age"])

      r3a = router.createRoute("/search/{name}/{age}/")
      expect(r3a.regex).toEqual(new RegExp("^/search/([^/]+)/([^/]+)/?$"))
      expect(r3a.parameters).toEqual(["name", "age"])

      r3b = router.createRoute("//search////{name}/{age}///")
      expect(r3b.regex).toEqual(new RegExp("^/search/([^/]+)/([^/]+)/?$"))
      expect(r3b.parameters).toEqual(["name", "age"])

      r4 = router.createRoute("/folder/{type}/{*path}")
      expect(r4.regex).toEqual(new RegExp("^/folder/([^/]+)/(.*)$"))
      expect(r4.parameters).toEqual(["type", "path"])

    it "match routes", () ->
      r1 = router.createRoute("/")
      expect(r1.match("")).toBeNull()
      expect(r1.match("/")).toEqual({})

      r2 = router.createRoute("/{name}")
      expect(r2.match("/age")).toEqual({ name: "age" })
      expect(r2.match("/age/")).toEqual({ name: "age" })
      expect(r2.match("/name")).toEqual({ name: "name" })
      expect(r2.match("/name/")).toEqual({ name: "name" })

      r3 = router.createRoute("/search/{name}/{age}")
      expect(r3.match("/")).toBeNull()
      expect(r3.match("/search")).toBeNull()
      expect(r3.match("/search/")).toBeNull()
      expect(r3.match("/search/tom")).toBeNull()
      expect(r3.match("/search/tom/")).toBeNull()
      expect(r3.match("/search/tom/23")).toEqual({ name: "tom", age: "23" })
      expect(r3.match("/search/tom/23/")).toEqual({ name: "tom", age: "23" })

    it "match catch all routes", () ->
      r = router.createRoute("/folder/{type}/{*path}")
      expect(r.match("/folder")).toBeNull()
      expect(r.match("/folder/test/")).toEqual({ type: "test", path: "" })
      expect(r.match("/folder/test/a")).toEqual({ type: "test", path: "a" })
      expect(r.match("/folder/test/b/")).toEqual({ type: "test", path: "b/" })
      expect(r.match("/folder/test/c/d")).toEqual({ type: "test", path: "c/d" })
      expect(r.match("/folder/test/e/f/")).toEqual({ type: "test", path: "e/f/" })

  describe "RouterService", () ->
    it "match routes", () ->
      router.routes = []
      router.addRoute("/", "home-tmpl", 1)
      router.addRoute("/about", "about-tmpl", 2)
      router.addRoute("/user/{userName}", "user-tmpl", 3)

      expect(router.matchRoute("/")).toEqual({ templateName: "home-tmpl", viewModelType: 1, parameters: { } })
      expect(router.matchRoute("/about")).toEqual({ templateName: "about-tmpl", viewModelType: 2, parameters: { } })
      expect(router.matchRoute("/user/tom")).toEqual({ templateName: "user-tmpl", viewModelType: 3, parameters: { userName: "tom" } })

      expect(router.matchRoute("/abc")).toBeNull
      expect(router.matchRoute("/abc/def")).toBeNull
      expect(router.matchRoute("/user/tom/abc")).toBeNull
