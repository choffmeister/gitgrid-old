package com.gitgrid

import org.specs2.specification.Scope
import com.gitgrid.mongodb._

class WithPreparedDatabase extends Scope with FutureHelpers {
  DefaultDatabase.drop()

  val users = await((1 to 5).map(i => Users.insert(createUser(i))))
  val projects = await((1 to 10).map(i => Projects.insert(createProject(i))))

  def createUser(i: Int) = new User(
    userName = s"user${i}",
    passwordHash = s"pass${i}",
    passwordSalt = "",
    firstName = s"First${i}",
    lastName = s"Last${i}"
  )

  def createProject(i: Int) = new Project(
    userId = users((i - 1) % users.length).id.get,
    canonicalName = s"project${i}",
    displayName = s"Project ${i}",
    description = s"This is the project #${i}."
  )
}
