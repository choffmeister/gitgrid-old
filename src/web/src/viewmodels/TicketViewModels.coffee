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

    add: () => api.post("/tickets", @ticket.toJS()) if @validate()
    modify: () => api.put("/tickets/#{@ticket.id()}", @ticket.toJS()) if @validate()
    remove: () => api.delete("/tickets/#{@ticket.id()}")

  return {
    TicketsViewModels: TicketsViewModels
    TicketViewModel: TicketViewModel
  }
