package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class UpdateHttp2Test extends FlatSpec with Matchers with ElasticDsl with SharedElasticSugar {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("hans").mappings(
      mapping("albums").fields(
        textField("name").stored(true)
      )
    )
  }.await

  http.execute(
    indexInto("hans/albums") fields "name" -> "intersteller" id 5
  ).await

  blockUntilCount(1, "hans")

  "an update request" should "support field based update" in {

    import UpdateHttpExecutables2._

    val client2 = HttpClient2.fromRestClient(http.rest)

    client2.execute {
      update(5).in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"
  }
}
