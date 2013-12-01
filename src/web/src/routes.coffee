define [
  "viewmodels/TicketViewModels"
  "viewmodels/ThrowingViewModel"
], (
  TicketViewModels
  ThrowingViewModel
) -> [
  ["/", "dashboard"]
  ["/about", "about"]
  ["/tickets", "tickets/list", TicketViewModels.TicketsViewModel]
  ["/tickets/create", "tickets/create", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}/edit", "tickets/edit", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}", "tickets/show", TicketViewModels.TicketViewModel]
  ["/throw", "throwing", ThrowingViewModel]
]
