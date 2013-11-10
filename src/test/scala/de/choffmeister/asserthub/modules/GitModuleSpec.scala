package de.choffmeister.asserthub.modules

import org.specs2.mutable._
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import de.choffmeister.asserthub.WebService

class GitModuleSpec extends SpecificationWithJUnit with Specs2RouteTest with WebService {
  def actorRefFactory = system

  "GitHubModule" should {
    "receive GitHub hooks" in {
      Post("/api/github/hook", FormData(Map("payload" -> payload1))) ~>
        addHeaders(List(
          GitHubHeaders.Delivery("da6a0f38-49ea-11e3-8e7c-1074c53a98bd"),
          GitHubHeaders.Event("push")
        )) ~> route ~> check
      {
        status === OK
        responseAs[String] === "12490674"
      }
    }
  }
  
  val payload1 = """|{ "after" : "d231ce397c6fd3b2c853ed1ffce0f3fc1bc3d192",
                    |  "before" : "171880fd69f5e0fa4ccf010ec4deb0fa1911ee0a",
                    |  "commits" : [ { "added" : [  ],
                    |        "author" : { "email" : "mail@choffmeister.de",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "committer" : { "email" : "mail@choffmeister.de",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "distinct" : true,
                    |        "id" : "612a38f878b322b89f8ec2dfafe3f0df51bbca69",
                    |        "message" : "Commit",
                    |        "modified" : [ "README.md" ],
                    |        "removed" : [  ],
                    |        "timestamp" : "2013-08-30T10:53:51-07:00",
                    |        "url" : "https://github.com/choffmeister/hook-test/commit/612a38f878b322b89f8ec2dfafe3f0df51bbca69"
                    |      },
                    |      { "added" : [ ".core-ci.yml" ],
                    |        "author" : { "email" : "thekwasti@googlemail.com",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "committer" : { "email" : "thekwasti@googlemail.com",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "distinct" : true,
                    |        "id" : "2213fed29f5d6aac6230793d1f73b635607b29f5",
                    |        "message" : "Create .core-ci.yml",
                    |        "modified" : [  ],
                    |        "removed" : [  ],
                    |        "timestamp" : "2013-09-03T08:09:24-07:00",
                    |        "url" : "https://github.com/choffmeister/hook-test/commit/2213fed29f5d6aac6230793d1f73b635607b29f5"
                    |      },
                    |      { "added" : [  ],
                    |        "author" : { "email" : "thekwasti@googlemail.com",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "committer" : { "email" : "thekwasti@googlemail.com",
                    |            "name" : "Christian Hoffmeister",
                    |            "username" : "choffmeister"
                    |          },
                    |        "distinct" : true,
                    |        "id" : "d231ce397c6fd3b2c853ed1ffce0f3fc1bc3d192",
                    |        "message" : "Update .core-ci.yml",
                    |        "modified" : [ ".core-ci.yml" ],
                    |        "removed" : [  ],
                    |        "timestamp" : "2013-09-03T08:20:27-07:00",
                    |        "url" : "https://github.com/choffmeister/hook-test/commit/d231ce397c6fd3b2c853ed1ffce0f3fc1bc3d192"
                    |      }
                    |    ],
                    |  "compare" : "https://github.com/choffmeister/hook-test/compare/171880fd69f5...d231ce397c6f",
                    |  "created" : false,
                    |  "deleted" : false,
                    |  "forced" : false,
                    |  "head_commit" : { "added" : [  ],
                    |      "author" : { "email" : "thekwasti@googlemail.com",
                    |          "name" : "Christian Hoffmeister",
                    |          "username" : "choffmeister"
                    |        },
                    |      "committer" : { "email" : "thekwasti@googlemail.com",
                    |          "name" : "Christian Hoffmeister",
                    |          "username" : "choffmeister"
                    |        },
                    |      "distinct" : true,
                    |      "id" : "d231ce397c6fd3b2c853ed1ffce0f3fc1bc3d192",
                    |      "message" : "Update .core-ci.yml",
                    |      "modified" : [ ".core-ci.yml" ],
                    |      "removed" : [  ],
                    |      "timestamp" : "2013-09-03T08:20:27-07:00",
                    |      "url" : "https://github.com/choffmeister/hook-test/commit/d231ce397c6fd3b2c853ed1ffce0f3fc1bc3d192"
                    |    },
                    |  "pusher" : { "name" : "none" },
                    |  "ref" : "refs/heads/master",
                    |  "repository" : { "created_at" : 1377879914,
                    |      "description" : "Simple repository to test the GitHub hook.",
                    |      "fork" : false,
                    |      "forks" : 0,
                    |      "has_downloads" : true,
                    |      "has_issues" : true,
                    |      "has_wiki" : true,
                    |      "id" : 12490674,
                    |      "master_branch" : "master",
                    |      "name" : "hook-test",
                    |      "open_issues" : 0,
                    |      "owner" : { "email" : "mail@choffmeister.de",
                    |          "name" : "choffmeister"
                    |        },
                    |      "private" : false,
                    |      "pushed_at" : 1378221628,
                    |      "size" : 224,
                    |      "stargazers" : 0,
                    |      "url" : "https://github.com/choffmeister/hook-test",
                    |      "watchers" : 0
                    |    }
                    |}""".stripMargin
}