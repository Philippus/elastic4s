package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.{Preference, ElasticDsl}

// examples of the count API in dot notation
class GetSqlDsl extends ElasticDsl {

  // returns document by id
  get id 45 from "index"

  // get specific version of an id
  get id 45 from "index" version 4

  // return specific fields only
  get id 45 from "index" fields Seq("name", "place")

  // return the document from the primary node
  get id 45 from "index" preference Preference.Primary

}
