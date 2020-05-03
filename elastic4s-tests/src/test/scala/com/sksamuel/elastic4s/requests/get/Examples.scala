package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.ElasticDsl

object Examples extends ElasticDsl {

  get("bands", "123")

  multiget(
    get("bands", "123"),
    get("bands", "164")
  )
}
