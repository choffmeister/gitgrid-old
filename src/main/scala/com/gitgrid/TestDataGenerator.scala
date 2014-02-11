package com.gitgrid

import com.gitgrid.managers._
import com.gitgrid.mongodb._
import scala.util.Random
import scala.io.Source
import com.gitgrid.util.ZipHelper

class TestDataGenerator

import scala.concurrent.ExecutionContext.Implicits.global

object TestDataGenerator {
  def generate(userCount: Int = 3, projectCount: Int = 5, ticketCount: Int = 10) {
    DefaultDatabase.drop()
    DefaultDatabase.create()

    val users = (1 to userCount).map { i =>
      User(
        id = Some(Entity.generateId()),
        userName = s"user${i}",
        passwordHash = s"pass${i}"
      )
    }.toList

    val projects = (1 to projectCount).map { i =>
      Project(
        id = Some(Entity.generateId()),
        userId = choose(users, i).id.get,
        canonicalName = s"project${i}",
        displayName = s"Project ${i}"
      )
    }.toList

    val tickets = (1 to ticketCount).map { i =>
      Ticket(
        id = Some(Entity.generateId()),
        projectId = chooseRandom(projects).id.get,
        userId = chooseRandom(users).id.get,
        title = s"Ticket ${i}",
        description = loremipsum(random.nextInt.abs % 10000)
      )
    }

    users.foreach(Users.insert(_))
    projects.foreach(Projects.insert(_))
    tickets.foreach(Tickets.insert(_))

    val repoNames = List("gitignore", "highlightjs")

    (1 to projects.length).foreach { i =>
      val project = choose(projects, i)
      val dir = new java.io.File(Config.repositoriesDir, project.id.get.stringify)
      val repoName = choose(repoNames, i)

      dir.delete()
      ZipHelper.unzip(classOf[TestDataGenerator].getResourceAsStream(s"/${repoName}.zip"), dir)
    }
  }

  lazy val random = new Random()

  def choose[T](l: List[T], i: Int): T = l((i - 1).abs % l.length)

  def chooseRandom[T](l: List[T]): T = l(random.nextInt.abs % l.length)

  def loremipsum(size: Int): String = if (size / loremipsum.length > 0) loremipsum + loremipsum(size - loremipsum.length) else loremipsum.substring(0, size)

  val loremipsum = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.

Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.

Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.

Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.

Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.

At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.

Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus.
"""
}
