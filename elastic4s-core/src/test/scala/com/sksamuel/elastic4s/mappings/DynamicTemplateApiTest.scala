package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.analyzers.SpanishLanguageAnalyzer
import com.sksamuel.elastic4s.mappings.dynamictemplate.DynamicTemplateBodyFn
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateApiTest extends WordSpec with Matchers with JsonSugar with ElasticApi {

  "dynamic templates" should {
    "support match" in {
      val temp = dynamicTemplate("es").mapping(
        dynamicTextField().analyzer(SpanishLanguageAnalyzer)
      ) matchMappingType "string" `match` "*_es"
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template.json")
    }
    "support match pattern" in {
      val temp = dynamicTemplate("es").mapping(
        dynamicTextField().analyzer(SpanishLanguageAnalyzer)
      ).matchMappingType("string").matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template_match_pattern.json")
    }
    "support dynamic type" in {
      val temp = dynamicTemplate("es", dynamicType.docValues(false)).matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template_dynamic_type.json")
    }
  }
}
