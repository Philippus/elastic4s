package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl

// examples of the count API in dot notation
class CountSqlDsl extends ElasticDsl {

  // count from an index and all types
  count from "index"

  // count from a specified index and type
  count from "index" / "type"

  // count from many indexes
  count from("index1", "index2")

  // count from an index with a query to narrow the count
  count from "index" query termQuery("term", "value")

  // count from an index and type, with a query and specifying the min score for documents to be counted
  count from "index" / "type" query termQuery("term", "value") minScore 2.3
}
