package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class UpdateTest extends FlatSpec with MockitoSugar with ElasticSugar with Eventually with Matchers {

  implicit val duration: Duration = 10.seconds

  implicit override val patienceConfig = PatienceConfig(timeout = scaled(Span(5, Seconds)))

  client.execute(
    bulk(
      index into "scifi/startrek" fields "character" -> "kirk" id 5,
      index into "scifi/starwars" fields "character" -> "lando" id 8
    )
  ).await

  blockUntilCount(2, "scifi")

  "an update request" should "add a field when a script assigns a value" ignore {

    client.execute {
      update id 5 in "scifi/startrek" script {
        script("ctx._source.birthplace = 'iowa'").lang("groovy")
      }
    }.await
    refresh("scifi")

    eventually {
      client.execute {
        search in "scifi" types "startrek" term "birthplace" -> "iowa"
      }.await.totalHits shouldBe 1
    }
  }

  it should "support scala seqs in script params" ignore {

    val friends = List("han", "leia")

    client.execute {
      update(8).in("scifi/starwars") script {
        script("ctx._source.friends = friends") params Map("friends" -> friends)
      }
    }
    refresh("scifi")

    eventually {
      client.execute {
        search in "scifi" types "starwars" term "friends" -> "leia"
      }.await.totalHits shouldBe 1
    }
  }

  it should "support field based update" in {

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
      hits = resp.totalHits
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
      hits = resp.totalHits
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
      hits = resp.totalHits
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
