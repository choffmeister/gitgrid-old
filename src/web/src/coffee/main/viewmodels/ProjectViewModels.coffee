define ["underscore", "api", "router", "ViewModelBase", "../models/Project"], (_, api, router, ViewModelBase, Project) ->
  class ProjectsViewModel extends ViewModelBase
    init: () =>
      api.get("/projects").then (data) => @projects = _.map(data, (t) -> new Project(t))

  class ProjectViewModel extends ViewModelBase
    init: (args) =>
      if args.projectId?
        api.get("/projects/#{args.projectId}").then (data) => @project = new Project(data)
      else
        @project = new Project()
        @done()

    add: () => if @validate()
      api.post("/projects", @project.toJS()).then (project) =>
        @notifySuccess("Created", "Successfully created new project")
        router.navigate("/#!/projects/#{project.id}")

    modify: () => if @validate()
      api.put("/projects/#{@project.id()}", @project.toJS()).then () =>
        @notifySuccess("Edited", "Successfully edited project")
        router.navigate("/#!/projects/#{@project.id()}")

    remove: () =>
      api.delete("/projects/#{@project.id()}").then () =>
        @notifySuccess("Deleted", "Successfully deleted project")
        router.navigate("/#!/projects")

  return {
    ProjectsViewModel: ProjectsViewModel
    ProjectViewModel: ProjectViewModel
  }
