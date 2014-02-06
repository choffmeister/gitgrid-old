package com.gitgrid.routes

import org.specs2.mutable._
import org.specs2.matcher._
import org.specs2.execute._
import org.specs2.specification.SpecificationStringContext
import org.specs2.specification.Fragments
import org.specs2.specification.DefaultExampleFactory
import org.squeryl.Table
import com.gitgrid._
import com.gitgrid.models.Dsl.transaction
import com.gitgrid.models._
import com.gitgrid.JsonProtocol._
import spray.http.StatusCodes._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.routing.Route
import spray.testkit.Specs2RouteTest
import java.sql.Timestamp
import java.util.Calendar
import spray.routing.authentication.UserPass
import spray.http.HttpCookie
import spray.http.HttpHeaders

object CrudRoutesSpec extends FragmentsBuilder with MustThrownMatchers with Specs2RouteTest with WebService {
  def actorRefFactory = system

  def userBefore(): Unit = {}
  def userCreate(i: Long) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}", "", "plain", s"First${i}", s"Last${i}")
  def userModify(u: User) = new User(u.id, u.userName + "-changed", u.email, u.passwordHash, u.passwordSalt, u.passwordHashAlgorithm, u.firstName, u.lastName)
  def userCompare(u1: User, u2: User) = u1.id == u2.id && u1.userName == u2.userName

  def projectBefore(): Unit = (1 to 5).foreach(i => Database.users.insert(userCreate(i)))
  def projectCreate(i: Long) = new Project(0L, s"P${i}", s"Project ${i}", s"This is project ${i}", i % 5 + 1, now)
  def projectModify(p: Project) = new Project(p.id, p.key, p.name + "-changed", p.description, p.creatorId, p.createdAt)
  def projectCompare(p1: Project, p2: Project) = p1.id == p2.id && p1.name == p2.name

  def ticketBefore(): Unit = (1 to 5).foreach(i => Database.users.insert(userCreate(i)))
  def ticketCreate(i: Long) = new Ticket(0L, s"ticket${i}", s"This is ticket ${i}", i % 5 + 1, now)
  def ticketModify(t: Ticket) = new Ticket(t.id, t.title + "-changed", t.description, t.creatorId, t.createdAt)
  def ticketCompare(t1: Ticket, t2: Ticket) = t1.id == t2.id && t1.title == t2.title

  def is: Fragments = {
    val userExamples = spec[User](Database.users, route, "users", userBefore, userCreate, userModify, userCompare)
    val projectExamples = spec[Project](Database.projects, route, "projects", projectBefore, projectCreate, projectModify, projectCompare)
    val ticketExamples = spec[Ticket](Database.tickets, route, "tickets", ticketBefore, ticketCreate, ticketModify, ticketCompare)

    return userExamples append projectExamples append ticketExamples
  }

  def spec[T <: Entity](
    table: Table[T],
    route: Route,
    name: String,
    before: () => Any,
    createEntity: Long => T,
    modifyEntity: T => T,
    compareEntities: (T, T) => Boolean
  )(implicit
    m1: Marshaller[T],
    um1: FromResponseUnmarshaller[T],
    um2: FromResponseUnmarshaller[Option[T]],
    um3: FromResponseUnmarshaller[List[T]]
  ): Fragments = {
    val listExample = s"list ${name}" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 5).map(i => table.insert(createEntity(i)))

        Get(s"/api/${name}") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[List[T]].map(_.id) === (1 to 5) }
      }
    }

    val listODataExample = s"list ${name} while respecting OData queries" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 20).map(i => table.insert(createEntity(i)))

        Get(s"/api/${name}?$$top=5&$$skip=10") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check {
          val res = responseAs[List[T]]

          status === OK
          res must haveSize(5)
          res.map(_.id) === (11 to 15)
        }
      }
    }

    val retrieveExample = s"return individual ${name}" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 5).map(i => table.insert(createEntity(i)))

        Get(s"/api/${name}/1") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[Option[T]].get.id === 1 }
        Get(s"/api/${name}/5") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[Option[T]].get.id === 5 }
        Get(s"/api/${name}/0") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === NotFound }
        Get(s"/api/${name}/6") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === NotFound }
      }
    }

    val createExample = s"create new ${name}" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 5).map(i => table.insert(createEntity(i)))
        val newEntity = createEntity(6)

        Post(s"/api/${name}", newEntity) ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[Option[T]].get.id === 6 }
        Get(s"/api/${name}") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[List[T]].map(_.id) === (1 to 6) }
        Get(s"/api/${name}/6") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[Option[T]].get.id === 6 }
      }
    }

    val deleteExample = s"delete ${name}" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 5).map(i => table.insert(createEntity(i)))

        Delete(s"/api/${name}/17") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === NotFound }
        Delete(s"/api/${name}/2") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === OK }
        Get(s"/api/${name}") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { responseAs[List[T]].length === 4 }
        Get(s"/api/${name}/2") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === NotFound }
      }
    }

    val updateExample = s"update list ${name}" in new WithDatabase {
      transaction {
        db.drop
        db.create
        before()
        val entities = (1 to 5).map(i => table.insert(createEntity(i)))
        val outdatedEntity = entities(3)
        val updatedEntity = modifyEntity(outdatedEntity)

        Put(s"/api/${name}/1", updatedEntity) ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === BadRequest }
        Get(s"/api/${name}/4") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check {
          val res = responseAs[Option[T]].get
          compareEntities(res, outdatedEntity) === true
          compareEntities(res, updatedEntity) === false
        }
        Put(s"/api/${name}/4", updatedEntity) ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check { status === OK }
        Get(s"/api/${name}/4") ~> addHeader(HttpHeaders.Cookie(session())) ~> route ~> check {
          val res = responseAs[Option[T]].get
          compareEntities(res, outdatedEntity) === false
          compareEntities(res, updatedEntity) === true
        }
      }
    }

    return listExample append listODataExample append retrieveExample append createExample append deleteExample append updateExample
  }

  def session(): HttpCookie = {
    var sessionId = ""

    Post("/api/auth/login", UserPass("user1", "pass1")) ~> route ~> check {
      val res = responseAs[AuthenticationResponse]
      val setCookieHeader = headers.find(h => h.name.toLowerCase == "set-cookie").get
      val cookie = setCookieHeader.asInstanceOf[HttpHeaders.`Set-Cookie`].cookie

      status === OK
      sessionId = cookie.content
    }

    HttpCookie("gitgrid-sid", sessionId)
  }

  def now(): Timestamp = {
    val cal = Calendar.getInstance()

    return new Timestamp(cal.getTimeInMillis())
  }
}
