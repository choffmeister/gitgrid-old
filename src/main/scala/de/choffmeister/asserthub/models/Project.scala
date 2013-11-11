package de.choffmeister.asserthub.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import java.sql.Timestamp

case class Project(
  id: Long,
  key: String,
  name: String,
  description: String,
  creatorId: Long,
  createdAt: Timestamp
) extends TimestampedEntity

trait ProjectRepository extends EntityRepository[Project] {
  val keyDef = projectKED
  val table = Database.projects
}
