define ["ModelBase"], (ModelBase) ->
  class Project extends ModelBase
    meta: () ->
      id:
        defaultValue: 0
      key:
        defaultValue: ""
        validation:
          required: true
          maxLength: 8
      name:
        defaultValue: ""
        validation:
          required: true
          maxLength: 128
      description:
        defaultValue: ""
        validation:
          maxLength: 65536
      creatorId:
        defaultValue: 0
      createdAt:
        defaultValue: new Date()
