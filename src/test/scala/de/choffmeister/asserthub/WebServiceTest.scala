package de.choffmeister.asserthub

import org.junit._
import org.junit.Assert._
import spray.testkit._
import spray.http._
import StatusCodes._
import de.choffmeister.asserthub.models.Dsl._
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.JsonProtocol._
import spray.httpx.SprayJsonSupport._

class WebServiceTest extends JUnitRouteTest with WebService with DatabaseAwareTest {
  def actorRefFactory = system
  
  @Test def testUserRestRoutes() {
    def createUser(i: Long) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"First${i}", s"Last${i}")
    
    transaction {
      Database.drop
      Database.create
      val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
    
      Get("/api/users") ~> route ~> check { assertEquals(users.map(_.id), responseAs[List[User]].map(_.id)) }
      Get("/api/users/1") ~> route ~> check { assertEquals(users(0).id, responseAs[User].id) }
      Get("/api/users/5") ~> route ~> check { assertEquals(users(4).id, responseAs[User].id) }
      Get("/api/users/0") ~> route ~> check { assertEquals(NotFound, status) }
      Get("/api/users/6") ~> route ~> check { assertEquals(NotFound, status) }
    
      val newUser = createUser(6)
      Post("/api/users", newUser) ~> route ~> check { assertEquals(6, responseAs[User].id) }
      Get("/api/users") ~> route ~> check { assertEquals(6, responseAs[List[User]].length) }
      Get("/api/users/6") ~> route ~> check { assertEquals(6, responseAs[User].id) }
      
      Delete("/api/users/2") ~> route ~> check { assertEquals(OK, status) }
      Get("/api/users") ~> route ~> check { assertEquals(5, responseAs[List[User]].length) }
      Get("/api/users/2") ~> route ~> check { assertEquals(NotFound, status) }

      val outdatedUser = users(3)
      val updatedUser = User(
        outdatedUser.id,
        outdatedUser.userName + "-new",
        outdatedUser.email,
        outdatedUser.passwordHash,
        outdatedUser.passwordSalt,
        outdatedUser.passwordHashAlgorithm,
        outdatedUser.firstName,
        outdatedUser.lastName)
          
      Put("/api/users/1", updatedUser) ~> route ~> check { assertEquals(BadRequest, status) }
      Put("/api/users/4", updatedUser) ~> route ~> check { assertEquals(OK, status) }
      Get("/api/users/4") ~> route ~> check {
        val res = responseAs[User]
        assertNotEquals(outdatedUser.userName, res.userName)
        assertEquals(updatedUser.userName, res.userName)
      }
    }
  }
}