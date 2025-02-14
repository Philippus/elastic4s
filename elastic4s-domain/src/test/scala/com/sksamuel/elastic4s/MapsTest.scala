package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ext.Maps
import org.scalatest
import org.scalatest.matchers.should.Matchers

class MapsTest extends scalatest.flatspec.AnyFlatSpec with Matchers {
  "Maps flatten function" should "support null values" in {
    val jsonMap: Map[String, Any] = Map(
      "settings" -> Map(
        "index" -> Map(
          "routing"                -> Map(
            "allocation" -> Map(
              "include"          -> Map(
                "_tier_preference" -> "data_content"
              ),
              "initial_recovery" -> Map(
                "_id" -> null
              )
            )
          ),
          "number_of_shards"       -> "1",
          "routing_partition_size" -> "1",
          "blocks"                 -> Map(
            "write" -> "true"
          ),
          "provided_name"          -> "test-two",
          "resize"                 -> Map(
            "source" -> Map(
              "name" -> "test-index",
              "uuid" -> "1234"
            )
          ),
          "creation_date"          -> "1234",
          "number_of_replicas"     -> "1",
          "uuid"                   -> "1234",
          "version"                -> Map(
            "created" -> "1234"
          )
        )
      )
    )
    Maps.flatten(jsonMap) shouldBe Map(
      "settings.index.resize.source.name"                          -> "test-index",
      "settings.index.resize.source.uuid"                          -> "1234",
      "settings.index.provided_name"                               -> "test-two",
      "settings.index.blocks.write"                                -> "true",
      "settings.index.routing_partition_size"                      -> "1",
      "settings.index.routing.allocation.include._tier_preference" -> "data_content",
      "settings.index.routing.allocation.initial_recovery._id"     -> null,
      "settings.index.number_of_replicas"                          -> "1",
      "settings.index.version.created"                             -> "1234",
      "settings.index.number_of_shards"                            -> "1",
      "settings.index.creation_date"                               -> "1234",
      "settings.index.uuid"                                        -> "1234"
    )
  }
}
