package com.gitgrid.mongodb

import reactivemongo.bson._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import spray.json._

case class Project(
  id: Option[BSONObjectID] = None,
  userId: BSONObjectID,
  canonicalName: String = "",
  displayName: String = "",
  description: String = ""
) extends Entity

object Projects extends ReactiveMongoEntityRepository[Project]("projects") {
  implicit val reader = ProjectBSONFormat.ProjectBSONReader
  implicit val writer = ProjectBSONFormat.ProjectBSONWriter

  def findByCanonicalName(canonicalName: String)(implicit ec: ExecutionContext): Future[Option[Project]] = coll.find(BSONDocument("canonicalName" -> canonicalName)).one[Project]

  override def beforeInsert(entity: Project): Project =
    if (entity.id.isDefined) entity
    else entity.copy(id = Some(BSONObjectID.generate))
  override def beforeUpdate(entity: Project): Project =
    entity
}

object ProjectBSONFormat {
  implicit object ProjectBSONReader extends BSONDocumentReader[Project] {
    def read(doc: BSONDocument) = Project(
      id = doc.getAs[BSONObjectID]("_id"),
      userId = doc.getAs[BSONObjectID]("userId").get,
      canonicalName = doc.getAs[String]("canonicalName").get,
      displayName = doc.getAs[String]("displayName").get,
      description = doc.getAs[String]("description").get
    )
  }

  implicit object ProjectBSONWriter extends BSONDocumentWriter[Project] {
    def write(obj: Project): BSONDocument = BSONDocument(
      "_id" -> obj.id,
      "userId" -> obj.userId,
      "canonicalName" -> obj.canonicalName,
      "displayName" -> obj.displayName,
      "description" -> obj.description
    )
  }
}
