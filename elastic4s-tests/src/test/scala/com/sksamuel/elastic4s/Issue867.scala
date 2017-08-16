package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.ElasticDsl._

import scala.concurrent.duration.Duration

object Issue867 extends App {

  val host = "search-dev-myown-dflwj7tcnfsdcmsq3nqlpvy3z4.eu-central-1.es.amazonaws.com"
  val port = 80
  val httpClient = HttpClient(ElasticsearchClientUri(host, port))

  val json = "{'test': 'json'}"

  val requestResult = httpClient.execute {
    indexInto("a-index" / "a-type") id "a-id" source json
  }.await(Duration.Inf)

  println(s"Result status: ${requestResult.result}")
}
