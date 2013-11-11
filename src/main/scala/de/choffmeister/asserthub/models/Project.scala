package de.choffmeister.asserthub.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

case class Project(
  id: Long,
  key: String,
  name: String,
  description: String
) extends Entity

trait ProjectRepository extends EntityRepository[Project] {
  val keyDef = projectKED
  val table = Database.projects
}
