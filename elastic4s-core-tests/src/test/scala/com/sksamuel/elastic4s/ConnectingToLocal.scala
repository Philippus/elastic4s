package com.sksamuel.elastic4s

object ConnectingToLocal extends App {

  import ElasticDsl._

  import scala.concurrent.ExecutionContext.Implicits.global

  val local = ElasticClient.local
  local.execute {
    create index "got"
  } map { _ =>
    local.execute {
      index into "got" fields "name" -> "tyrion"
    }
  }

  Thread.sleep(3000)

  val remote = ElasticClient.remote("elasticsearch://127.0.0.1:9300")
  remote.execute {
    search in "got"
  }.map { resp =>
    println(resp.hits)
  }
}
