package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class GetTest extends FlatSpec with MockitoSugar with ElasticSugar {

    client.execute {
        index into "beer/lager" fields (
          "name" -> "coors light",
          "brand" -> "coors"
          ) id 4
    }
    client.execute {
        index into "beer/lager" fields (
          "name" -> "bud lite",
          "brand" -> "bud"
          ) id 8
    }
    refresh("music")
    blockUntilCount(2, "music")

    "a search index" should "retrieve a document by id" in {

        val resp = client.sync.execute {
            get id 8 from "beer/lager"
        }
        assert("8" === resp.getId)
    }
}
