package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.handlers.update.{UpdateBuilderFn, UpdateByQueryBodyFn}
import com.sksamuel.elastic4s.requests.script.Script
import org.scalatest.wordspec.AnyWordSpec

class UpdateByQueryBodyFnTest extends AnyWordSpec with JsonSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "update by query" should {
    "generate correct body" when {
      "script is specified" in {
        val q = updateIn("test").query(matchQuery("field", 123)).script(Script("script", Some("painless")))
        UpdateByQueryBodyFn(q).string should matchJson(
          """{"query":{"match":{"field":{"query":123}}},"script":{"lang":"painless","source":"script"}}"""
        )
      }
      "script is not specified" in {
        val q = updateIn("test").query(matchQuery("field", 123))
        UpdateByQueryBodyFn(q).string should matchJson(
          """{"query":{"match":{"field":{"query":123}}}}"""
        )
      }
    }
  }

  "update by id" should {
    "generate correct body" when {
      "script update" in {
        val q = updateById("test", "1234")
          .script(Script("script", Some("painless")))

        UpdateBuilderFn(q).string should matchJson(
          """{"script":{"lang":"painless","source":"script"}}"""
        )
      }

      "script upsert" in {
        val q = updateById("test", "1234")
          .script(Script("script", Some("painless")))
          .scriptedUpsert(true)

        UpdateBuilderFn(q).string should matchJson(
          """{"script":{"lang":"painless","source":"script"},"upsert":{},"scripted_upsert":true}"""
        )
      }

      "script upsert set to false" in {
        val q = updateById("test", "1234")
          .script(Script("script", Some("painless")))
          .scriptedUpsert(false)

        UpdateBuilderFn(q).string should matchJson(
          """{"script":{"lang":"painless","source":"script"},"scripted_upsert":false}"""
        )
      }

      "doc update" in {
        val q = updateById("test", "1234")
          .doc("foo" -> "bar")

        UpdateBuilderFn(q).string should matchJson(
          """{"doc":{"foo":"bar"}}"""
        )
      }

      "doc upsert" in {
        val q = updateById("test", "1234")
          .doc("foo" -> "bar")
          .docAsUpsert(true)

        UpdateBuilderFn(q).string should matchJson(
          """{"doc":{"foo":"bar"},"doc_as_upsert":true}"""
        )
      }

      "doc upsert set to false" in {
        val q = updateById("test", "1234")
          .doc("foo" -> "bar")
          .docAsUpsert(false)

        UpdateBuilderFn(q).string should matchJson(
          """{"doc":{"foo":"bar"},"doc_as_upsert":false}"""
        )
      }

      "doc update and upsert" in {
        val q = updateById("test", "1234")
          .doc("foo" -> "bar")
          .upsert("foo" -> "baz")

        UpdateBuilderFn(q).string should matchJson(
          """{"doc":{"foo":"bar"},"upsert":{"foo":"baz"}}"""
        )
      }
    }
  }
}
