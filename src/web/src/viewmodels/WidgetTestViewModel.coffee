define ["jquery", "knockout", "ViewModelBase"], ($, ko, ViewModelBase) ->
  class WidgetTestViewModel extends ViewModelBase
    init: () ->
      @foo = @observableArray(["1", "2", "3"])
      @userName = @observable("user")
      @description = @observable("My description.")
      @birthday = @observable("01.02.2013")
      @tags = @observableArray(["scala", "coffeescript"])
      @tags2 = @observableArray([2])
      @tags3 = @observableArray([])
      @group = @observable(2)
      @group2 = @observable(null)

      @json = @computed () =>
        raw = {}
        for k, v of this
          if k != "json" and ko.isObservable(v)
            raw[k] = ko.unwrap(v)
        JSON.stringify(raw, true, 4)

    query: (query, callback) =>
      console.log(query)
      callback([{ value: query + " One", text: query + " One" }, { value: query + " Two", text: query + " Two" }])
