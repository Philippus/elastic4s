package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class MultiGetDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "coldplay/albums" fields ("name" -> "mylo xyloto") id 5
  }.await
  client.execute {
    index into "coldplay/albums" fields ("name" -> "x&y") id 3
  }.await

  refresh("coldplay")
  blockUntilCount(2, "coldplay")

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums",
        get id 5 from "coldplay/albums",
        get id 34 from "coldplay/albums"
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(3 === resp.getResponses.size)
    assert("3" === resp.getResponses.toSeq(0).getResponse.getId)
    assert("5" === resp.getResponses.toSeq(1).getResponse.getId)
    assert(!resp.getResponses.toSeq(2).getResponse.isExists)
  }
}
