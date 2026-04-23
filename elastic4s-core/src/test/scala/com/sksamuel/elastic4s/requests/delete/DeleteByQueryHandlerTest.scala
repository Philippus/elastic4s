package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.handlers.delete.DeleteHandlers
import com.sksamuel.elastic4s.requests.common.Preference.Shards
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchAllQuery
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeleteByQueryHandlerTest extends AnyFlatSpec with Matchers with DeleteHandlers {

  it should "build preference parameter with ShardsPreferenceRequest" in {
    val request = DeleteByQueryRequest(
      indexes = "test_index",
      query = MatchAllQuery()
    ).preference(Shards(List("0", "1", "2", "3")))

    val elasticRequest = DeleteByQueryHandler.build(request)

    elasticRequest.params should contain("preference" -> "_shards:0,1,2,3")
  }

  it should "not include preference parameter when shards is None" in {
    val request = DeleteByQueryRequest(
      indexes = "test_index",
      query = MatchAllQuery()
    )

    val elasticRequest = DeleteByQueryHandler.build(request)

    elasticRequest.params should not contain key("preference")
  }
}
