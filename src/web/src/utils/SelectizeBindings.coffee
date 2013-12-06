define ["jquery", "underscore", "knockout"], ($, _, ko) ->
  setOptions = (selectize, opts) ->
    type = Object.prototype.toString.call(opts)
    switch type
      when "[object Array]"
        for o in _.map(opts, (x) => { value: x, text: x })
          selectize.addOption(o)
      when "[object Object]"
        for o in _.map(opts, (v, k) => { value: k, text: v })
          selectize.addOption(o)
      when "[object Function]"
        if ko.isObservable(opts)
          opts.subscribe (changes) ->
            for change in changes
              switch change.status
                when "added" then selectize.addOption({ value: change.value, text: change.value })
                when "deleted" then selectize.removeOption(change.value)
          , null, "arrayChange"
          setOptions(selectize, ko.unwrap(opts))
        else
          setOptions(selectize, opts())
      else throw new Error("Invalid object for selectizeOptions")

  ko.bindingHandlers.selectize =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)

      selectize = $(element).selectize({
        persist: false
      })[0].selectize
      setOptions(selectize, allBindings.get("selectizeOptions"))

      $(element).on "change", () => observable(selectize.getValue())
      selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      selectize = $(element).selectize()[0].selectize

      selectize.setValue(value)

  ko.bindingHandlers.selectizeMultiText =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)

      selectize = $(element).selectize({
        delimiter: ","
        persist: false
        create: (input) =>
          value: input
          text: input
      })[0].selectize

      $(element).on "change", () =>
        observable(selectize.getValue().split(","))
      for v in value
        selectize.addOption({ value: v, text: v })
      selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      console.log("update")
      observable = valueAccessor()
      value = ko.unwrap(observable)
      selectize = element.selectize

      for v in value
        selectize.addOption({ value: v, text: v })
      selectize.setValue(value)

  return null
