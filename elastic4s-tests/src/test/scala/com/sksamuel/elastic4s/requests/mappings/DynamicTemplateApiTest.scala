package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.analysis.LanguageAnalyzers
import com.sksamuel.elastic4s.fields.{DynamicField, TextField}
import com.sksamuel.elastic4s.handlers.index.mapping.DynamicTemplateBodyFn
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DynamicTemplateApiTest extends AnyWordSpec with Matchers with JsonSugar with ElasticApi {

  "dynamic templates" should {
    "support match" in {
      val temp = DynamicTemplateRequest("es", TextField("match_*_es").analyzer(LanguageAnalyzers.spanish)) matchMappingType "string" `match` "*_es"
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template.json")
    }
    "support match pattern" in {
      val temp = DynamicTemplateRequest("es",
        TextField("matchPattern_*_es").analyzer(LanguageAnalyzers.spanish)
      ).matchMappingType("string").matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template_match_pattern.json")
    }
    "support dynamic type" in {
      val temp = DynamicTemplateRequest("es", DynamicField("", docValues = Some(false))).matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string() should matchJsonResource("/json/mappings/dynamic_template_dynamic_type.json")
    }
  }
}
