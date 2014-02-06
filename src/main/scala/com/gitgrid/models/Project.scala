package com.gitgrid.models

import org.squeryl.Query
import org.squeryl.KeyedEntityDef
import org.squeryl.annotations.Column
import com.gitgrid.models._
import com.gitgrid.models.Dsl._
import java.sql.Timestamp

case class Project(
  id: Long,
  key: String,
  name: String,
  @Column(length = 65536)
  description: String,
  creatorId: Long,
  createdAt: Timestamp
) extends Entity with TimestampedEntity with OwnedEntity

trait ProjectRepository extends EntityRepository[Project] {
  val keyDef = projectKED
  val table = Database.projects
}
