package de.choffmeister.asserthub

import org.specs2.mutable.SpecificationWithJUnit
import de.choffmeister.asserthub.models._
import de.choffmeister.asserthub.models.Dsl.transaction
import de.choffmeister.asserthub.JsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.testkit._
import spray.http._
import StatusCodes._

class WebServiceSpec extends SpecificationWithJUnit with Specs2RouteTest with WebService {
  def actorRefFactory = system

  "WebService" should {
    "accept valid login credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create
        val users = (1 to 5).map(i => db.users.insert(createUser(i)))
    
        Post("/api/auth/login", ("user1", "pass1")) ~> route ~> check {
          val res = responseAs[Option[User]]
          
          res.isDefined === true
          res.get.id === 1
          res.get.userName == "user1"
        }
        
        Post("/api/auth/login", ("user2", "pass2")) ~> route ~> check {
          val res = responseAs[Option[User]]
          
          res.isDefined === true
          res.get.id === 2
          res.get.userName == "user2"
        }
      }
    }
    
    "reject invalid login credentials" in new WithDatabase {
      transaction {
        db.drop
        db.create
        val users = (1 to 5).map(i => db.users.insert(createUser(i)))
    
        Post("/api/auth/login", ("user1", "pass2")) ~> route ~> check {
          status === Unauthorized
        }
                
        Post("/api/auth/login", ("user2", "pass1")) ~> route ~> check {
          status === Unauthorized
        }
        
        Post("/api/auth/login", ("unknown", "pass")) ~> route ~> check {
          status === Unauthorized
        }
      }
    }
    
    "handle auth logout requests" in {
      Post("/api/auth/logout") ~> route ~> check {
        responseAs[String] === "logout"
      }
    }
    
    "handle auth state requests" in {
      Get("/api/auth/state") ~> route ~> check {
        responseAs[String] === "state"
      }
    }
    
    "list users" in new WithDatabase {
      transaction {
        Database.drop
        Database.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
    
        Get("/api/users") ~> route ~> check { responseAs[List[User]].map(_.id) === (1 to 5) }
      }
    }
    
    "return individual users" in new WithDatabase {
      transaction {
        Database.drop
        Database.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
    
        Get("/api/users/1") ~> route ~> check { responseAs[User].id === 1 }
        Get("/api/users/5") ~> route ~> check { responseAs[User].id === 5 }
        Get("/api/users/0") ~> route ~> check { status === NotFound }
        Get("/api/users/6") ~> route ~> check { status === NotFound }
      }
    }
        
    "create new users" in new WithDatabase {
      transaction {
        Database.drop
        Database.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
        val newUser = createUser(6)
        
        Post("/api/users", newUser) ~> route ~> check { responseAs[User].id === 6 }
        Get("/api/users") ~> route ~> check { responseAs[List[User]].map(_.id) === (1 to 6) }
        Get("/api/users/6") ~> route ~> check { responseAs[User].id === 6 }
      }
    }
            
    "delete users" in new WithDatabase {
      transaction {
        Database.drop
        Database.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
    
        Delete("/api/users/2") ~> route ~> check { status === OK }
        Get("/api/users") ~> route ~> check { responseAs[List[User]].length === 4 }
        Get("/api/users/2") ~> route ~> check { status === NotFound }
      }
    }
      
    "update list users" in new WithDatabase {
      transaction {
        Database.drop
        Database.create
        val users = (1 to 5).map(i => Database.users.insert(createUser(i)))
    
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
          
        Put("/api/users/1", updatedUser) ~> route ~> check { status === BadRequest }
        Put("/api/users/4", updatedUser) ~> route ~> check { status === OK }
        Get("/api/users/4") ~> route ~> check {
          val res = responseAs[User]
          res.userName !== "user4"
          res.userName === "user4-new"
        }
      }
    }
  }
  
  def createUser(i: Long) = new User(0L, s"user${i}", s"user${i}@invalid.domain.tld", s"pass${i}", "", "plain", s"First${i}", s"Last${i}")
}
