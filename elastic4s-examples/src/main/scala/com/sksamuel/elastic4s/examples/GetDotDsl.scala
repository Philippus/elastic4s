package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.{ElasticDsl, Preference}

// examples of the count API in dot notation
class GetDotDsl extends ElasticDsl {

  // returns document by id
  get(45).from("index")

  // get specific version of an id
  get(45).from("index").version(4)

  // return specific fields only
  get(45).from("index").fields("name", "place")

  // return the document from the primary node
  get(45).from("index").preference(Preference.Primary)

}
