package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl

// examples of the count API in dot notation
class ValidateDotDsl extends ElasticDsl {

  // simple query being validated
  validateIn("index" / "type").query("id:123")

  // validating boolean query
  validateIn("index" / "type").query(
    bool {
      must {
        termQuery("name", "sammy")
      } should {
        termQuery("place", "buckinghamshire")
      }
    }
  )
}


