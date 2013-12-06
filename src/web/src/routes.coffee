define [
  "viewmodels/TicketViewModels"
  "viewmodels/ProjectViewModels"
  "viewmodels/GitBrowserViewModel"
  "viewmodels/ThrowingViewModel"
], (
  TicketViewModels
  ProjectViewModels
  GitBrowserViewModel
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
  ["/projects/{projectId}/tree/{refOrSha}/{*path}", "git/tree", GitBrowserViewModel, { type: "tree" }]
  ["/projects/{projectId}/blob/{refOrSha}/{*path}", "git/blob", GitBrowserViewModel, { type: "blob" }]
  ["/throw", "throwing", ThrowingViewModel]
]
