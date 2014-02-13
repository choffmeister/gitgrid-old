package com.gitgrid.mongodb

import reactivemongo.api.indexes._
import reactivemongo.bson._
import scala.concurrent._

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

  def findByFullQualifiedName(userName: String, canonicalName: String)(implicit ec: ExecutionContext): Future[Option[Project]] = {
    Users.findByUserName(userName).flatMap(_ match {
      case Some(user) => coll.find(BSONDocument("userId" -> user.id.get, "canonicalName" -> canonicalName)).one[Project]
      case _ => Future(None)
    })
  }

  override def beforeInsert(entity: Project): Project =
    if (entity.id.isDefined) entity
    else entity.copy(id = Some(BSONObjectID.generate))
  override def beforeUpdate(entity: Project): Project =
    entity

  override def indexes(implicit ec: ExecutionContext) = {
    coll.indexesManager.ensure(Index(List("userId" -> IndexType.Ascending, "canonicalName" -> IndexType.Ascending), unique = true))
  }
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
