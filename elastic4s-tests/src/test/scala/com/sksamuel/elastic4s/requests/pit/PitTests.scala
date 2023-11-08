package com.sksamuel.elastic4s.requests.pit

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt
import scala.util.Try

class PitTests extends AnyFlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("beer")
    }.await
  }

  client.execute {
    createIndex("beer").mapping {
      mapping(
        textField("name").stored(true),
        textField("brand").stored(true),
        textField("ingredients").stored(true)
      )
    }
  }.await

  "A create pit request" should "create a pit" in {
    val resp = client
      .execute(createPointInTime("beer").keepAlive(30.seconds))
      .await.result

    resp.id.length should be > 0
  }

  "A delete pit request" should "delete a pit" in {
    val pit = client
      .execute(createPointInTime("beer").keepAlive(30.seconds))
      .await.result

    val result = client.execute(deletePointInTime(pit.id)).await.result

    result.succeeded should be(true)
    result.num_freed should be > 0
  }
}
