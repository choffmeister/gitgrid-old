package de.choffmeister.asserthub

import java.sql.DriverManager

import org.junit.After
import org.junit.Before
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter

/**
 * Registers a database session factory that creates a temporary H2
 * in memory database. The database name contains the current thread
 * ID and hence every JUnit test has its own database.
 */
trait DatabaseAwareTest {
  @Before def before() {
    Class.forName("org.h2.Driver")

    SessionFactory.concreteFactory = Some(() => {
      val currentThread = Thread.currentThread
      val id = currentThread.getId

      // ensure that each unit test thread has is own temporary database
      Session.create(DriverManager.getConnection(s"jdbc:h2:mem:${id};DB_CLOSE_DELAY=-1"), new H2Adapter())
    })
  }
}