package com.gitgrid.testutils

import akka.actor._
import akka.util._
import org.specs2.specification.Scope
import scala.concurrent.duration._

class WithTestActorSystem extends Scope with AsyncUtils {
  implicit val system = ActorSystem("testsystem")
  implicit val timeout = Timeout(1.seconds)
}
