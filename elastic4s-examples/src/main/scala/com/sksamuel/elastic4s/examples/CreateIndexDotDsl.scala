package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings.FieldType.{IntegerType, StringType}

// examples of the count API in dot notation
class CreateIndexDotDsl extends ElasticDsl {

  // create index with no mappings and with shards / replica sets
  createIndex("tweets").shards(3).replicas(4)

  // create index with no mappings but refresh interval set
  createIndex("tweets").refreshInterval("5s")

  // create index with two mappings, each with two fields
  createIndex("index").mappings(
    "tweets" as(
      "name" typed StringType,
      "userId" typed IntegerType
      ),
    "users" as(
      "userId" typed IntegerType,
      "username" typed StringType
      )
  )

  // create index with copy_to functionaliy
  createIndex("tweets").mappings(
    mapping("tweet").as(
      field("title").typed(StringType).index("analyzed").copyTo("meta_data", "article_info")
    )
  )
}
