package com.gitgrid.util

import java.io._
import scala.collection.JavaConversions._
import org.eclipse.jgit.storage.file._
import org.eclipse.jgit.internal.storage.pack._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.treewalk._
import org.eclipse.jgit.api._
import java.util.Date

abstract class GitObject {
  val id: String
}

abstract class GitObjectType
case object GitCommitObjectType extends GitObjectType
case object GitTreeObjectType extends GitObjectType
case object GitBlobObjectType extends GitObjectType
case object GitTagObjectType extends GitObjectType

object GitObjectType {
  def apply(i: Int): GitObjectType = i match {
    case 1 => GitCommitObjectType
    case 2 => GitTreeObjectType
    case 3 => GitBlobObjectType
    case 4 => GitTagObjectType
    case _ => throw new Exception("GitObjectType '$i' is not supported")
  }
}

case class GitRef(name: String, id: String)
case class GitCommitSignature(name: String, email: String, when: Date, timeZone: Int)
case class GitCommit(id: String, parents: List[String], tree: String, author: GitCommitSignature, committer: GitCommitSignature, fullMessage: String, shortMessage: String) extends GitObject
case class GitTree(id: String, entries: List[GitTreeEntry]) extends GitObject
case class GitTreeEntry(id: String, name: String, fileMode: String, objectType: GitObjectType)
case class GitBlob(id: String) extends GitObject {
  def readAsStream[T](repo: GitRepository)(inner: InputStream => T): T = {
    val stream = repo.jgit.open(ObjectId.fromString(id)).openStream()
    try {
      inner(stream)
    } finally {
      stream.close()
    }
  }

  def readAsBytes(repo: GitRepository): Seq[Byte] = {
    repo.jgit.open(ObjectId.fromString(id)).getBytes().toSeq
  }

  def readAsString(repo: GitRepository): String = {
    new String(repo.jgit.open(ObjectId.fromString(id)).getBytes(), "UTF-8")
  }
}

class GitRepository(val dir: File) {
  val builder = new FileRepositoryBuilder()
  val jgit = builder.setGitDir(dir).readEnvironment().findGitDir().build()
  val reader = jgit.newObjectReader()

  def resolve(refOrSha: String): String = {
    Option(jgit.resolve(refOrSha)) match {
      case Some(oid) => oid.getName
      case _ => Option(jgit.getRef("refs/heads/" + refOrSha)) match {
        case Some(ref) => ref.getObjectId.getName
        case _ => Option(jgit.getRef("refs/tags/" + refOrSha)) match {
          case Some(ref) => ref.getObjectId.getName
          case _ => Option(jgit.getRef(refOrSha)) match {
            case Some(ref) => ref.getObjectId.getName
            case _ => throw new Exception(s"Unknown ref '$refOrSha'")
          }
        }
      }
    }
  }

  def commit(id: String): GitCommit = {
    createCommitWalk { walk =>
      Option(walk.parseCommit(ObjectId.fromString(id))) match {
        case Some(commit) => convertGitCommit(commit)
        case _ => throw new Exception(s"Unknown commit '$id'")
      }
    }
  }

  // TODO handle peeled refs
  def branches(drop: Option[Int] = None, take: Option[Int] = None): List[GitRef] = {
    page(new Git(jgit).branchList().call(), drop, take).map(ref => GitRef(ref.getName, ref.getObjectId.getName)).toList
  }

  // TODO handle peeled refs
  def tags(drop: Option[Int] = None, take: Option[Int] = None): List[GitRef] = {
    page(new Git(jgit).tagList().call(), drop, take).map(ref => GitRef(ref.getName, ref.getObjectId.getName)).toList
  }

  def commits(drop: Option[Int] = None, take: Option[Int] = None): List[GitCommit] = {
    page(new Git(jgit).log().call(), drop, take).map(convertGitCommit(_)).toList
  }

  def tree(id: String): GitTree = {
    val entries = createTreeParser(ObjectId.fromString(id))(convertGitTreeEntry(_))
    GitTree(id, entries)
  }

  def blob(id: String): GitBlob = {
    GitBlob(id)
  }

  def traverse(commit: GitCommit, path: String): GitObject = {
    traverse(tree(commit.tree), path)
  }

  @scala.annotation.tailrec
  private def traverse(currentTree: GitTree, path: String): GitObject = {
    if (path == "") throw new Exception("Path must not be empty")
    else if (path == "/") currentTree
    else {
      val recursionInfo = path.indexOf("/", 1) match {
        case i if i < 0 =>
          val segment = path.substring(1)
          val entry = currentTree.entries.find(_.name == segment).get
          (segment, None, entry)
        case i =>
          val segment = path.substring(1, i)
          val rest = path.substring(i)
          val entry = currentTree.entries.find(_.name == segment).get
          (segment, Some(rest), entry)
      }

      recursionInfo match {
        case (segment, Some(rest), GitTreeEntry(id, _, _, GitTreeObjectType)) => traverse(tree(id), rest)
        case (segment, None, GitTreeEntry(id, _, _, GitTreeObjectType)) => tree(id)
        case (segment, None, GitTreeEntry(id, _, _, GitBlobObjectType)) => blob(id)
        case _ => throw new Exception()
      }
    }
  }

  def close() {
    reader.release()
    jgit.close()
  }

  private def page[T](it: Iterable[T], drop: Option[Int], take: Option[Int]): Iterable[T] = (drop, take) match {
    case (Some(drop), Some(take)) => it.drop(drop).take(take)
    case (Some(drop), None) => it.drop(drop)
    case (None, Some(take)) => it.take(take)
    case (None, None) => it
  }

  private def createCommitWalk[T](inner: RevWalk => T): T = {
    val walk = new RevWalk(reader)
    try {
      inner(walk)
    } finally {
      walk.release()
      walk.dispose()
    }
  }

  private def createTreeParser[T](id: ObjectId)(inner: CanonicalTreeParser => T): List[T] = {
    val parser = new CanonicalTreeParser()
    parser.reset(reader, id)
    var list = List.empty[T]
    while (!parser.eof) {
      list = list ::: List(inner(parser))
      parser.next()
    }
    list
  }

  private def convertGitCommit(commit: RevCommit): GitCommit =
    GitCommit(
      commit.getName,
      commit.getParents.map(_.getName).toList,
      commit.getTree.getName,
      GitCommitSignature(
        commit.getAuthorIdent.getName,
        commit.getAuthorIdent.getEmailAddress,
        commit.getAuthorIdent.getWhen,
        commit.getAuthorIdent.getTimeZoneOffset
      ),
      GitCommitSignature(
        commit.getCommitterIdent.getName,
        commit.getCommitterIdent.getEmailAddress,
        commit.getCommitterIdent.getWhen,
        commit.getCommitterIdent.getTimeZoneOffset
      ),
      commit.getFullMessage,
      commit.getShortMessage
    )

  private def convertGitTreeEntry(parser: CanonicalTreeParser): GitTreeEntry =
    GitTreeEntry(
      parser.getEntryObjectId.getName,
      parser.getEntryPathString,
      parser.getEntryFileMode.toString,
      GitObjectType(parser.getEntryFileMode.getObjectType)
    )
}

object GitRepository {
  def apply[T](dir: File)(inner: GitRepository => T): T = {
    val repo = new GitRepository(dir)
    try {
      inner(repo)
    } finally {
      repo.close()
    }
  }

  def init[T](dir: File, bare: Boolean) {
    Git.init().setBare(bare).setDirectory(dir).call()
  }
}
