package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.analyzers.SpanishLanguageAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.TextType
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateApiTest extends WordSpec with Matchers with JsonSugar with ElasticApi {

  "dynamic templates" should {
    "generate correct json" in {
      val temp = DynamicTemplateDefinition("es",
        dynamicTemplateMapping(TextType) analyzer SpanishLanguageAnalyzer
      ) matchMappingType "string" matching "*_es"
      temp.build.string should matchJsonResource("/json/mappings/dynamic_template.json")
    }
    "support multiple dynamic templates" in {

    }
  }
}
