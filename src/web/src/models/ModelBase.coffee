define ["jquery", "knockout"], ($, ko) ->
  class ModelBase
    constructor: (data) ->
      # merge default values and given concrete data (concrete data wins)
      raw = $.extend({}, @default(), data)

      # wrap raw values in observables
      ko.mapping.fromJS(raw, {}, this)

      # annotate with validation rules
      for property, rule of @validation()
        this[property].extend(rule)

    fromJS: (data) => ko.mapping.fromJS(data, this)
    toJS: () => ko.mapping.toJS(this)

    # Implement in subclasses and return a JSON object with default values.
    # Note that only properties that are available in the default object or
    # given via the data parameter in the constructor are converted from and
    # to JSON.
    default: () -> {}

    # Implement in subclass and return a JSON object with key value pairs
    # where keys are the property names and values the validation rules
    validation: () -> {}

    validate: () =>
      errors = ko.validation.group(this)
      errors.showAllMessages()
      errors().length == 0
