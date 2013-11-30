define ["jquery", "api", "vm", "ViewModelBase"], ($, api, vm, ViewModelBase) ->
  class DashboardViewModel extends ViewModelBase
    init: () =>
      @page = @observable(0)
      @tickets = @observableArray([])

      @page.subscribe (page) =>
        api.get("/tickets?$top=10&$skip=#{page * 10}")
          .done (res) =>
            @tickets(res)
          .fail (err) =>
            vm.showNotification(true, "Could not fetch data")

      return api.get("/tickets?$top=10&$skip=0").done((res) => @tickets(res)).promise()
