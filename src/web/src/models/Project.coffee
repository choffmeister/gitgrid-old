define ["ModelBase"], (ModelBase) ->
  class Ticket extends ModelBase
    default: () ->
      id: 0
      title: ""
      key: ""
      name: ""
      description: ""
      creatorId: 0
      createdAt: new Date()

    validation: () ->
      name:
        required: true
        maxLength: 128
      key:
        required: true
        maxLength: 8
      description:
        maxLength: 65536
