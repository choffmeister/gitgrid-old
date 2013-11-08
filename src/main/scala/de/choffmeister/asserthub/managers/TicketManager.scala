package de.choffmeister.asserthub.managers

import org.squeryl.Query
import org.squeryl.KeyedEntityDef

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

object TicketManager extends EntityRepository[Ticket] {
  val keyDef = ticketKED
  val table = Database.tickets
}
