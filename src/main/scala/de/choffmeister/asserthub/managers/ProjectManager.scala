package de.choffmeister.asserthub.managers

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._

object ProjectManager extends ProjectRepository {
  def createProject(key: String, name: String, creatorId: Long): Project = {
    val p = new Project(0L, key, name, "", creatorId, now)
    insert(p)
  }
}
