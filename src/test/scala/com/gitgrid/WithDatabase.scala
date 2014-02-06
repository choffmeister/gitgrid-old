package com.gitgrid

import java.sql.DriverManager

import org.specs2.specification.Scope
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter

import com.gitgrid.models.Database

/**
 * Registers a database session factory that creates a temporary H2
 * in memory database. The database name contains the current thread
 * ID and hence two specs2 scopes cannot run on the same database in
 * parallel.
 */
trait WithDatabase extends Scope {
  import com.gitgrid.models.Dsl._

  val db = Database

  setSessionFactory()

  def setSessionFactory() = {
    Class.forName("org.h2.Driver")

    SessionFactory.concreteFactory = Some(() => {
      val currentThread = Thread.currentThread
      val id = currentThread.getId

      // ensure that each unit test thread has is own temporary database
      Session.create(DriverManager.getConnection(s"jdbc:h2:mem:${id};DB_CLOSE_DELAY=-1"), new H2Adapter())
    })
  }
}
