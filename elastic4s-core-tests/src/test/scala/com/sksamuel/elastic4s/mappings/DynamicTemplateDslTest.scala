package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analyzers.SpanishLanguageAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateDslTest extends WordSpec with Matchers with JsonSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "dynamic templates" should {
    "generate correct json" in {
      val temp = DynamicTemplateDefinition("es",
        dynamicTemplateMapping(StringType) analyzer SpanishLanguageAnalyzer
      ) matchMappingType "string" matching "*_es"
      temp.build.string should matchJsonResource("/json/mappings/dynamic_template.json")
    }
  }
}
