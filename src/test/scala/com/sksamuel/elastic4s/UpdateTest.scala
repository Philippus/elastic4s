package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class UpdateTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.sync.execute(
    bulk(
      index into "scifi/startrek" fields "character" -> "kirk" id 5,
      index into "scifi/starwars" fields "character" -> "lando" id 8
    )
  )

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  blockUntilCount(2, "scifi")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "an update request" should "add a field when a script assigns a value" in {

    client.sync.execute {
      update id 5 in "scifi/startrek" script "ctx._source.birthplace = 'iowa'"
    }
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.sync.search {
        search in "scifi" types "startrek" term "birthplace" -> "iowa"
      }
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "support doc based update" in {

    client.sync.execute {
      update(8).in("scifi/starwars").doc(
        "character" -> "lando",
        "location" -> "cloud city"
      )
    }
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.sync.search {
        search in "scifi" types "starwars" term "location" -> "cloud"
      }
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "keep existing fields with partial update" in {
    client.sync.execute {
      update(5).in("scifi/startrek").docAsUpsert(
        "bestmate" -> "spock"
      )
    }
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.sync.search {
        search in "scifi" types "startrek" term "character" -> "kirk"
      }
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "insert non existent doc when using docAsUpsert" in {

    client.sync.execute {
      update(14).in("scifi/starwars").doc(
        "character" -> "chewie"
      ).docAsUpsert
    }
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client.sync.search {
        search in "scifi" types "starwars" term "character" -> "chewie"
      }
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }

  it should "not insert non existent doc when using doc" in {
    val e = intercept[RuntimeException] {
      client.sync.execute {
        update(55).in("scifi/lostinspace").doc(
          "character" -> "smith"
        )
      }
      refresh("scifi")
    }
    assert(e != null)
  }
}
