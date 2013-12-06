package de.choffmeister.asserthub.managers

import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl._
import de.choffmeister.asserthub.Config
import de.choffmeister.asserthub.GitRepository

object ProjectManager extends ProjectRepository {
  def createProject(key: String, name: String, description: String, creatorId: Long): Project = {
    val p = insert(new Project(0L, key, name, description, creatorId, now))
    GitRepository.init(new java.io.File(Config.repositoriesDir, p.id.toString), true)
    p
  }
}
