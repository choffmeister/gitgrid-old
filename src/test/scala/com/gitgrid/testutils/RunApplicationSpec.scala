package com.gitgrid.testutils

import org.specs2.mutable._
import com.gitgrid.Application

trait RunApplicationSpec extends Specification {
  def runApplication[T](inner: Application => T): T = {
    val app = new Application()
    try {
      app.startup()
      // TODO: find a more robust way to know when the app is bootstrapped
      Thread.sleep(5000)
      inner(app)
    } finally {
      app.shutdown()
    }
  }
}
