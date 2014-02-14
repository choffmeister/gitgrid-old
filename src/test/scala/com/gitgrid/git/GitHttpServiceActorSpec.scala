package com.gitgrid.git

import akka.actor._
import akka.pattern.ask
import akka.testkit._
import com.gitgrid.testutils._
import org.specs2.mutable._
import spray.http._
import spray.http.HttpMethods._
import spray.http.StatusCodes._

class GitHttpServiceActorSpec extends Specification {
  sequential

  "reject invalid requests" in new WithTestActorSystem {
    val actorRef = TestActorRef[GitHttpServiceActor]

    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1"))).asInstanceOf[HttpResponse].status === BadRequest
    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1.git"))).asInstanceOf[HttpResponse].status === BadRequest
  }

  "reject dump Git HTTP requests" in new WithTestActorSystem {
    val actorRef = TestActorRef[GitHttpServiceActor]

    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1.git/info/refs"))).asInstanceOf[HttpResponse].status === Forbidden
  }

  "reject invalid smart Git HTTP requests" in new WithTestActorSystem {
    val actorRef = TestActorRef[GitHttpServiceActor]

    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1.git/info/refs?service=git-upload-packX"))).asInstanceOf[HttpResponse].status === BadRequest
    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1.git/info/refs?service=git-receive-packX"))).asInstanceOf[HttpResponse].status === BadRequest
  }

  "handle proper smart Git HTTP requests" in new WithTestActorSystem {
    val actorRef = TestActorRef[GitHttpServiceActor]

    await(actorRef ? HttpRequest(method = GET, uri = Uri("/user1/project1.git/info/refs?service=git-upload-pack"))).asInstanceOf[HttpResponse].status === OK
  }
}
