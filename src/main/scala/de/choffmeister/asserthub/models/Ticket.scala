package de.choffmeister.asserthub.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import java.sql.Timestamp

case class Ticket(
  id: Long,
  title: String,
  creatorId: Long,
  createdAt: Timestamp
) extends TimestampedEntity

trait TicketRepository extends EntityRepository[Ticket] {
  val keyDef = ticketKED
  val table = Database.tickets
}
