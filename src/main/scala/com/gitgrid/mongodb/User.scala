package com.gitgrid.mongodb

import reactivemongo.api.indexes._
import reactivemongo.bson._
import scala.concurrent._

case class User(
  id: Option[BSONObjectID] = None,
  userName: String = "",
  passwordHash: String = "",
  passwordSalt: String = "",
  firstName: String = "",
  lastName: String = ""
) extends Entity

object Users extends ReactiveMongoEntityRepository[User]("users") {
  implicit val reader = UserBSONFormat.UserBSONReader
  implicit val writer = UserBSONFormat.UserBSONWriter

  def findByUserName(userName: String)(implicit ec: ExecutionContext): Future[Option[User]] = coll.find(BSONDocument("userName" -> userName)).one[User]

  override def beforeInsert(entity: User): User =
    if (entity.id.isDefined) entity
    else entity.copy(id = Some(BSONObjectID.generate))
  override def beforeUpdate(entity: User): User =
    entity

  override def indexes(implicit ec: ExecutionContext) = {
    coll.indexesManager.ensure(Index(List("userName" -> IndexType.Ascending), unique = true))
  }
}

object UserBSONFormat {
  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument) = User(
      id = doc.getAs[BSONObjectID]("_id"),
      userName = doc.getAs[String]("userName").get,
      passwordHash = doc.getAs[String]("passwordHash").get,
      passwordSalt = doc.getAs[String]("passwordSalt").get,
      firstName = doc.getAs[String]("firstName").get,
      lastName = doc.getAs[String]("lastName").get
    )
  }

  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    def write(obj: User): BSONDocument = BSONDocument(
      "_id" -> obj.id.get,
      "userName" -> obj.userName,
      "passwordHash" -> obj.passwordHash,
      "passwordSalt" -> obj.passwordSalt,
      "firstName" -> obj.firstName,
      "lastName" -> obj.lastName
    )
  }
}
