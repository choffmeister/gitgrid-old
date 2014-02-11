package com.gitgrid

import java.io.File
import java.util.concurrent.TimeUnit
import com.typesafe.config.ConfigFactory

object Config {
  lazy val raw = ConfigFactory.load("application")

  lazy val requestTimeout = raw.getDuration("spray.can.server.request-timeout", TimeUnit.MILLISECONDS)
  lazy val repositoriesDir = new File(raw.getString("gitgrid.repositoriesDir"))
  lazy val mongoDbServers = List(raw.getString("gitgrid.mongodb.host") + ":" + raw.getInt("gitgrid.mongodb.port"))
  lazy val mongoDbDatabase = raw.getString("gitgrid.mongodb.database")
}
