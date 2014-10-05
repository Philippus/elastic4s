package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

import scala.concurrent.duration._

/** @author Stephen Samuel */
class UpdateTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.execute(
    bulk(
      index into "scifi/startrek" fields "character" -> "kirk" id 5,
      index into "scifi/starwars" fields "character" -> "lando" id 8
    )
  ).await

  blockUntilCount(2, "scifi")

  "an update request" should "add a field when a script assigns a value" in {

    client.execute {
      update id 5 in "scifi/startrek" script "ctx._source.birthplace = 'iowa'" lang "groovy"
    }.await
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.execute {
        search in "scifi" types "startrek" term "birthplace" -> "iowa"
      }.await
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "support doc based update" in {

    client.execute {
      update(8).in("scifi/starwars").doc(
        "character" -> "lando",
        "location" -> "cloud city"
      )
    }.await
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.execute {
        search in "scifi" types "starwars" term "location" -> "cloud"
      }.await
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "keep existing fields with partial update" in {
    client.execute {
      update(5).in("scifi/startrek").docAsUpsert(
        "bestmate" -> "spock"
      )
    }.await
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.execute {
        search in "scifi" types "startrek" term "character" -> "kirk"
      }.await
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "insert non existent doc when using docAsUpsert" in {

    client.execute {
      update(14).in("scifi/starwars").doc(
        "character" -> "chewie"
      ).docAsUpsert
    }.await
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.execute {
        search in "scifi" types "starwars" term "character" -> "chewie"
      }.await
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "not insert non existent doc when using doc" in {
    val e = intercept[RuntimeException] {
      client.execute {
        update(55).in("scifi/lostinspace").doc(
          "character" -> "smith"
        )
      }.await
      refresh("scifi")
    }
    assert(e != null)
  }
}
