package com.gitgrid

import org.specs2.specification.Scope
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import com.gitgrid.mongodb._

class WithPreparedDatabase extends Scope {
  DefaultDatabase.drop()

  val users = await((1 to 5).map(i => Users.insert(createUser(i))))

  def createUser(i: Long) = new User(
    userName = s"user${i}",
    passwordHash = s"pass${i}",
    passwordSalt = "",
    firstName = s"First${i}",
    lastName = s"Last${i}"
  )

  def await[T](future: Future[T]): T = Await.result(future, Inf)
  def await[T](futures: Seq[Future[T]]): Seq[T] = futures.map(f => Await.result(f, Inf))
}
