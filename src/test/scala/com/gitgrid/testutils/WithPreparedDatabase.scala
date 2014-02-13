package com.gitgrid.testutils

import com.gitgrid.mongodb._
import org.specs2.specification.Scope

class WithPreparedDatabase extends Scope with AsyncUtils {
  DefaultDatabase.drop()
  DefaultDatabase.create()

  val users = await((1 to 5).map(i => Users.insert(createUser(i))))
  val projects =
    await((1 to 10).map(i => Projects.insert(createProject(i, i)))) ++
    await((1 to 10).map(i => Projects.insert(createProject(i, i + 3))))

  def createUser(i: Int) = new User(
    userName = s"user${i}",
    passwordHash = s"pass${i}",
    passwordSalt = "",
    firstName = s"First${i}",
    lastName = s"Last${i}"
  )

  def createProject(i: Int, j: Int) = new Project(
    userId = users((j - 1).abs % users.length).id.get,
    canonicalName = s"project${i}",
    displayName = s"Project ${i}",
    description = s"This is the project #${i}."
  )
}
