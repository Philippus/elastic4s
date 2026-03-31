package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.handlers.update.UpdateHandlers
import com.sksamuel.elastic4s.requests.common.Preference.Shards
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchAllQuery
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UpdateByQueryHandlerTest extends AnyFlatSpec with Matchers with UpdateHandlers {

  it should "build preference parameter with ShardsPreferenceRequest" in {
    val request = UpdateByQueryAsyncRequest(
      indexes = "test_index",
      query = MatchAllQuery()
    ).preference(Shards(List("0", "1", "2", "3")))

    val elasticRequest = AsyncUpdateByQueryHandler.build(request)

    elasticRequest.params should contain("preference" -> "_shards:0,1,2,3")
  }

  it should "not include preference parameter when shards is None" in {
    val request = UpdateByQueryAsyncRequest(
      indexes = "test_index",
      query = MatchAllQuery()
    )

    val elasticRequest = AsyncUpdateByQueryHandler.build(request)

    elasticRequest.params should not contain key("preference")
  }
}
