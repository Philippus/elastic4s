package com.sksamuel.elastic4s.http.update

import com.sksamuel.elastic4s.http.JsonSugar
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.scalatest.WordSpec

class UpdateByQueryBodyFnTest extends WordSpec with JsonSugar {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  "update by query" should {
    "generate correct body" when {
      "script is specified" in {
        val q = updateIn("test" / "type").query(matchQuery("field", 123)).script(ScriptDefinition("script", Some("painless")))
        UpdateByQueryBodyFn(q).string() should matchJson(
          """{"query":{"match":{"field":{"query":123}}},"script":{"lang":"painless","inline":"script"}}"""
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
