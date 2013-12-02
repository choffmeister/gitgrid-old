define [
  "viewmodels/TicketViewModels"
  "viewmodels/ProjectViewModels"
  "viewmodels/ThrowingViewModel"
], (
  TicketViewModels
  ProjectViewModels
  ThrowingViewModel
) -> [
  ["/", "dashboard"]
  ["/about", "about"]
  ["/tickets", "tickets/list", TicketViewModels.TicketsViewModel]
  ["/tickets/create", "tickets/create", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}/edit", "tickets/edit", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}", "tickets/show", TicketViewModels.TicketViewModel]
  ["/projects", "projects/list", ProjectViewModels.ProjectsViewModel]
  ["/projects/create", "projects/create", ProjectViewModels.ProjectViewModel]
  ["/projects/{projectId}/edit", "projects/edit", ProjectViewModels.ProjectViewModel]
  ["/projects/{projectId}", "projects/show", ProjectViewModels.ProjectViewModel]
  ["/throw", "throwing", ThrowingViewModel]
]
