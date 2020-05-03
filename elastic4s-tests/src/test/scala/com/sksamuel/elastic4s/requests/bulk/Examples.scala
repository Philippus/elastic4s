package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.ElasticDsl

object Examples extends ElasticDsl {

  bulk(
    indexInto("bands").fields("name" -> "coldplay"),
    deleteById("bands", "123"),
    indexInto("bands").fields(
      "name" -> "elton john",
      "best_album" -> "tumbleweed connection"
    )
  )

  bulk(
    indexInto("bands").fields("name" -> "coldplay"),
    indexInto("bands").fields("name" -> "kings of leon"),
    deleteById("places", "3"),
    deleteById("artists", "2"),
    updateById("bands", "4").doc("name" -> "kate bush")
  )
}
