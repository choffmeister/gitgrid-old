package de.choffmeister.asserthub.managers

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

object TicketManager extends TicketRepository {
  def createTicket(title: String, creatorId: Long): Ticket = {
    val t = new Ticket(0L, title, creatorId, now)
    insert(t)
  }
}