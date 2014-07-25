package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class ValidateTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.execute {
    index into "food/pasta" fields (
      "name" -> "maccaroni",
      "color" -> "yellow"
    )
  }.await

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  blockUntilCount(1, "food")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a validate query" should "return valid when the query is valid" in {

    val resp = client.execute {
      validate in "food/pasta" query "maccaroni"
    }.await
    assert(true === resp.isValid)
  }
}
