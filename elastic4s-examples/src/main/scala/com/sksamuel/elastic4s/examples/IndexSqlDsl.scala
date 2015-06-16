package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl

import scala.concurrent.duration._

class IndexSqlDsl extends ElasticDsl {

  // simple index of one field
  index into "index" / "type" fields "name" -> "sammy"

  // index with an explicit version
  index into "index" / "type" fields "name" -> "sammy" version 5

  // index using a type with implicit Indexable
  val somedoc = Document("a", "b", "c")
  index into "index" / "type" source somedoc

  // setting a ttl using scala durations, and using a map for fields
  index into "index" / "type" ttl 15.seconds fields Map("name" -> "sammy", "location" -> "bucks")
}
