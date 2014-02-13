package com.gitgrid.mongodb

import reactivemongo.bson._
import scala.concurrent._

case class Ticket(
  id: Option[BSONObjectID] = None,
  projectId: BSONObjectID,
  userId: BSONObjectID,
  title: String = "",
  description: String = ""
) extends Entity

object Tickets extends ReactiveMongoEntityRepository[Ticket]("tickets") {
  implicit val reader = TicketBSONFormat.TicketBSONReader
  implicit val writer = TicketBSONFormat.TicketBSONWriter

  override def beforeInsert(entity: Ticket): Ticket =
    if (entity.id.isDefined) entity
    else entity.copy(id = Some(BSONObjectID.generate))
  override def beforeUpdate(entity: Ticket): Ticket =
    entity
}

object TicketBSONFormat {
  implicit object TicketBSONReader extends BSONDocumentReader[Ticket] {
    def read(doc: BSONDocument) = Ticket(
      id = doc.getAs[BSONObjectID]("_id"),
      projectId = doc.getAs[BSONObjectID]("projectId").get,
      userId = doc.getAs[BSONObjectID]("userId").get,
      title = doc.getAs[String]("title").get,
      description = doc.getAs[String]("description").get
    )
  }

  implicit object TicketBSONWriter extends BSONDocumentWriter[Ticket] {
    def write(obj: Ticket): BSONDocument = BSONDocument(
      "_id" -> obj.id,
      "projectId" -> obj.projectId,
      "userId" -> obj.userId,
      "title" -> obj.title,
      "description" -> obj.description
    )
  }
}
