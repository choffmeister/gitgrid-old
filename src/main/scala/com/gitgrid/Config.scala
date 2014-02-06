package com.gitgrid

import java.io.File
import com.typesafe.config.ConfigFactory

object Config {
  lazy val raw = ConfigFactory.load("application")

  lazy val repositoriesDir = new File(raw.getString("gitgrid.repositoriesDir"))
}
