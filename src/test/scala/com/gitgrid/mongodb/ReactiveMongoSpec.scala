package com.gitgrid.mongodb

import org.specs2.mutable._

import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global

class ReactiveMongoSpec extends SpecificationWithJUnit {
  "ReactiveMongo" should {
    "work" in {
      ok
    }
  }
}
