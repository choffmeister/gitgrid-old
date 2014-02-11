package com.gitgrid

import java.io.File
import com.typesafe.config.ConfigFactory

object Config {
  lazy val raw = ConfigFactory.load("application")

  lazy val repositoriesDir = new File(raw.getString("gitgrid.repositoriesDir"))
  lazy val mongoDbServers = List(Config.raw.getString("gitgrid.mongodb.host") + ":" + Config.raw.getInt("gitgrid.mongodb.port"))
  lazy val mongoDbDatabase = Config.raw.getString("gitgrid.mongodb.database")
}
