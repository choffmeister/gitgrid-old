define ["jquery", "underscore", "knockout"], ($, _, ko) ->
  detectMode = (element) ->
    tagName = $(element).prop("tagName").toLowerCase()
    switch tagName
      when "select" then "single"
      when "input" then "multi"
      else throw new Error("SelectizeBinding cannot be applied to element of type '#{tagName}")

  compareArrays = (a, b) -> _.all(_.zip(a, b), (x) -> x[0] == x[1])

  hasChanged = (selectize, mode, newValue) ->
    switch mode
      when "single" then selectize.getValue() != newValue
      when "multi" then not compareArrays(selectize.getValue().split(","), newValue)

  ko.bindingHandlers.selectize =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      mode = detectMode(element)

      optionsDefault =
        persist: false
        create: false
      options = $.extend(optionsDefault, allBindings.get("selectizeOptions") || {})
      options.create = true unless options.options or options.load

      selectize = $(element).selectize(options)[0].selectize

      switch mode
        when "single"
          $(element).on "change", () =>
            observable(selectize.getValue())
          selectize.setValue(value)
        when "multi"
          $(element).on "change", () =>
            observable(selectize.getValue().split(","))
          for v in value
            selectize.addOption({ value: v, text: v })
          selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      mode = detectMode(element)
      selectize = $(element).selectize()[0].selectize

      if hasChanged(selectize, mode, value)
        switch mode
          when "single"
            selectize.setValue(value)
          when "multi"
            for v in value
              selectize.addOption({ value: v, text: v })
            selectize.setValue(value)

  return null
