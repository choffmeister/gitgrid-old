package com.gitgrid.managers

import com.gitgrid.models._
import com.gitgrid.models.Dsl._
import com.gitgrid.Config
import com.gitgrid.git.GitRepository

object ProjectManager extends ProjectRepository {
  def createProject(key: String, name: String, description: String, creatorId: Long): Project = {
    val p = insert(new Project(0L, key, name, description, creatorId, now))
    GitRepository.init(new java.io.File(Config.repositoriesDir, p.id.toString), true)
    p
  }
}
