define [
  "viewmodels/DashboardViewModel"
  "viewmodels/TicketViewModels"
  "viewmodels/ThrowingViewModel"
], (
  DashboardViewModel
  TicketViewModels
  ThrowingViewModel
) -> [
  ["/", "dashboard", DashboardViewModel]
  ["/about", "about"]
  ["/create", "tickets/create", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}", "tickets/show", TicketViewModels.TicketViewModel]
  ["/throw", "throwing", ThrowingViewModel]
]
