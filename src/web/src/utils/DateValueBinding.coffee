define ["jquery", "knockout"], ($, ko) ->
  ko.bindingHandlers.datevalue =
    init: (element, valueAccessor, allBindings) ->
      value = ko.unwrap(valueAccessor())

      # use default value binding
      ko.bindingHandlers.value.init(element, valueAccessor, allBindings)

      $(element).datepicker
        weekStart: 1,
        format: "dd.mm.yyyy",
        autoclose: true,
        todayHighlight: true

    update: (element, valueAccessor, allBindings) ->
      value = ko.unwrap(valueAccessor())

      # use default value binding
      ko.bindingHandlers.value.update(element, valueAccessor, allBindings)

      $(element).datepicker("update")

  return null
