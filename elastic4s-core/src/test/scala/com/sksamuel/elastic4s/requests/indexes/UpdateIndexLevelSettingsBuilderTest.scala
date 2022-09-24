package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.handlers.index.UpdateIndexLevelSettingsBuilder
import com.sksamuel.elastic4s.requests.admin.{TranslogRequest, UpdateIndexLevelSettingsRequest}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class UpdateIndexLevelSettingsBuilderTest extends AnyFunSuite with Matchers {

  test("build UpdateIndexLevelSettingsRequest without translog") {
    val definition = UpdateIndexLevelSettingsRequest(
      indexes = Seq("test_index"),
      numberOfReplicas = Some(0),
      autoExpandReplicas = Some("0-all"),
      refreshInterval = Some("30s"),
      maxResultWindow = Some(100),
      translog = None
    )

    UpdateIndexLevelSettingsBuilder(definition).string() shouldBe """{"index":{"number_of_replicas":0,"auto_expand_replicas":"0-all","refresh_interval":"30s","max_result_window":100}}"""
  }

  test("build UpdateIndexLevelSettingsRequest with translong") {
    val definition = UpdateIndexLevelSettingsRequest(
      indexes = Seq("test_index"),
      numberOfReplicas = Some(0),
      autoExpandReplicas = Some("0-all"),
      refreshInterval = Some("30s"),
      maxResultWindow = Some(100),
      translog = Some(TranslogRequest(
        durability = "request",
        syncInterval = Some("5s"),
        flushThresholdSize = Some("512mb"),
      ))
    )
    UpdateIndexLevelSettingsBuilder(definition).string() shouldBe """{"index":{"number_of_replicas":0,"auto_expand_replicas":"0-all","refresh_interval":"30s","max_result_window":100,"translog":{"durability":"request","sync_interval":"5s","flush_threshold_size":"512mb"}}}"""
  }
}
