define ["ModelBase"], (ModelBase) ->
  class Project extends ModelBase
    config: () ->
      id:
        default: 0
      key:
        default: ""
        validation:
          required: true
          maxLength: 8
      name:
        default: ""
        validation:
          required: true
          maxLength: 128
      description:
        default: ""
        validation:
          maxLength: 65536
      creatorId:
        default: 0
      createdAt:
        default: new Date()
