package com.sksamuel.elastic4s.http.update

import com.sksamuel.elastic4s.http.JsonSugar
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.update.UpdateByQueryBodyFn
import org.scalatest.WordSpec

class UpdateByQueryBodyFnTest extends WordSpec with JsonSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "update by query" should {
    "generate correct body" when {
      "script is specified" in {
        val q = updateIn("test" / "type").query(matchQuery("field", 123)).script(Script("script", Some("painless")))
        UpdateByQueryBodyFn(q).string() should matchJson(
          """{"query":{"match":{"field":{"query":123}}},"script":{"lang":"painless","source":"script"}}"""
        )
      }
      "script is not specified" in {
        val q = updateIn("test" / "type").query(matchQuery("field", 123))
        UpdateByQueryBodyFn(q).string() should matchJson(
          """{"query":{"match":{"field":{"query":123}}}}"""
        )
      }
    }
  }
}
