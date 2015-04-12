package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.{ElasticDsl, JsonSugar, SpanishLanguageAnalyzer}
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateDslTest extends WordSpec with Matchers with JsonSugar {

  "dynamic templates" should {
    "generate correct json" in {
      import ElasticDsl._
      val temp = new DynamicTemplateDefinition("es") matching "*_es" matchMappingType "string" mapping {
        field typed StringType analyzer SpanishLanguageAnalyzer
      }
      val str = temp.build.string()
      println(str)
      str should matchJsonResource("/json/mappings/dynamic_template.json")
    }
  }

}
