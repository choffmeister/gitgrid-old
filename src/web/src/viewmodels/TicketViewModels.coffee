define ["underscore", "api", "ViewModelBase", "../models/Ticket"], (_, api, ViewModelBase, Ticket) ->
  class TicketsViewModels extends ViewModelBase
    init: () =>
      api.get("/tickets").then (data) => @tickets = _.map(data, (t) -> new Ticket(t))

  class TicketViewModel extends ViewModelBase
    init: (args) =>
      if args.ticketId?
        api.get("/tickets/#{args.ticketId}").then (data) => @ticket = new Ticket(data)
      else
        @ticket = new Ticket()
        @done()

    add: () => if @validate()
      api.post("/tickets", @ticket.toJS()).then () =>
        @notifySuccess("Created", "Successfully created new ticket")
        @redirect("/")

    modify: () => if @validate()
      api.put("/tickets/#{@ticket.id()}", @ticket.toJS()).then () =>
        @notifySuccess("Modified", "Successfully modified ticket")

    remove: () => api.delete("/tickets/#{@ticket.id()}")

  return {
    TicketsViewModels: TicketsViewModels
    TicketViewModel: TicketViewModel
  }
