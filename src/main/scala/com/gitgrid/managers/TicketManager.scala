package com.gitgrid.managers

import com.gitgrid.models._
import com.gitgrid.models.Dsl._

object TicketManager extends TicketRepository {
  def createTicket(title: String, description: String, creatorId: Long): Ticket = {
    val t = new Ticket(0L, title, description, creatorId, now)
    insert(t)
  }
}
