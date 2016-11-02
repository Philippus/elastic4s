package com.sksamuel.elastic4s2.mappings

import com.sksamuel.elastic4s2.analyzers.SpanishLanguageAnalyzer
import com.sksamuel.elastic4s2.mappings.FieldType.StringType
import com.sksamuel.elastic4s2.{ElasticDsl2$, JsonSugar}
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateDslTest extends WordSpec with Matchers with JsonSugar {

  "dynamic templates" should {
    "generate correct json" in {
      import ElasticDsl2._
      val temp = DynamicTemplateDefinition("es",
        field typed StringType analyzer SpanishLanguageAnalyzer
      ) matchMappingType "string" matching "*_es"
      temp.build.string should matchJsonResource("/json/mappings/dynamic_template.json")
    }
  }
}
