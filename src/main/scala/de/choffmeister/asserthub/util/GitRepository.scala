package de.choffmeister.asserthub.util

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

case class GitCommitSignature(name: String, email: String, when: Date, timeZone: Int)
case class GitCommit(id: String, parents: List[String], tree: String, author: GitCommitSignature, committer: GitCommitSignature, fullMessage: String, shortMessage: String) extends GitObject
case class GitTree(id: String, entries: List[GitTreeEntry]) extends GitObject
case class GitTreeEntry(id: String, name: String, fileMode: String, objectType: GitObjectType)
case class GitBlob(id: String) extends GitObject {
  def readAsStream[T](repo: GitRepository)(inner: InputStream => T): T = {
    val stream = repo.repo.open(ObjectId.fromString(id)).openStream()
    try {
      inner(stream)
    } finally {
      stream.close()
    }
  }

  def readAsBytes(repo: GitRepository): Seq[Byte] = {
    repo.repo.open(ObjectId.fromString(id)).getBytes().toSeq
  }

  def readAsString(repo: GitRepository): String = {
    new String(repo.repo.open(ObjectId.fromString(id)).getBytes(), "UTF-8")
  }
}

class GitRepository(val dir: File) {
  val builder = new FileRepositoryBuilder()
  val repo = builder.setGitDir(dir).readEnvironment().findGitDir().build()
  val reader = repo.newObjectReader()

  def resolve(refOrSha: String): String = {
    Option(repo.resolve(refOrSha)) match {
      case Some(oid) => oid.getName
      case _ => Option(repo.getRef("refs/heads/" + refOrSha)) match {
        case Some(ref) => ref.getObjectId.getName
        case _ => throw new Exception(s"Unknown commit '$refOrSha'")
      }
    }
  }

  def commit(id: String): GitCommit = {
    createCommitWalk { walk =>
      Option(walk.parseCommit(ObjectId.fromString(id))) match {
        case Some(commit) => GitCommit(
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
       case _ => throw new Exception(s"Unknown commit '$id'")
      } 
    }
  }

  def tree(id: String): GitTree = {
    val entries = createTreeParser(ObjectId.fromString(id)) { parser =>
      GitTreeEntry(
        parser.getEntryObjectId.getName,
        parser.getEntryPathString,
        parser.getEntryFileMode.toString,
        GitObjectType(parser.getEntryFileMode.getObjectType)
      )
    }
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
    repo.close()
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
      list = inner(parser) :: list
      parser.next()
    }
    list
  }
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