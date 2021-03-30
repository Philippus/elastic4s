package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.analyzers.{EnglishLanguageAnalyzer, SpanishLanguageAnalyzer}
import com.sksamuel.elastic4s.requests.indexes.CreateIndexContentBuilder
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MappingDefinitionDslTest extends AnyWordSpec with Matchers with JsonSugar with ElasticApi {

  "mapping definition" should {
    "insert source exclusion directives when set" in {
      val mapping = MappingDefinition().sourceExcludes("excludeMe1", "excludeMe2")
      val output = MappingBuilderFn.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
    }
    "insert source exclusion directives when set and override enabled directive" in {
      val mapping = MappingDefinition().sourceExcludes("excludeMe1", "excludeMe2").source(true)
      val output = MappingBuilderFn.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
      output should not include """"enabled":true"""
    }
    "insert source enabling" in {
      val mapping = MappingDefinition().source(false)
      val output = MappingBuilderFn.build(mapping).string()
      output should not include """"_source":{"excludes":["excludeMe1","excludeMe2"]}"""
      output should include(""""enabled":false""")
    }
    "not insert date detection by default" in {
      val mapping = MappingDefinition()
      val output = MappingBuilderFn.build(mapping).string()
      output should not include "date"
    }
    "insert date detection when set to true" in {
      val mapping = MappingDefinition().dateDetection(true)
      val output = MappingBuilderFn.build(mapping).string()
      output should include("""date_detection":true""")
    }
    "insert date detection when set to false" in {
      val mapping = MappingDefinition().dateDetection(false)
      val output = MappingBuilderFn.build(mapping).string()
      output should include("""date_detection":false""")
    }
    "not insert numeric detection by default" in {
      val mapping = MappingDefinition()
      val output = MappingBuilderFn.build(mapping).string()
      output should not include "numeric"
    }
    "insert numeric detection when set to true" in {
      val mapping = MappingDefinition().numericDetection(true)
      val output = MappingBuilderFn.build(mapping).string()
      output should include("""numeric_detection":true""")
    }
    "insert numeric detection when set to false" in {
      val mapping = MappingDefinition().numericDetection(false)
      val output = MappingBuilderFn.build(mapping).string()
      output should include("""numeric_detection":false""")
    }
//    "include dynamic templates" in {
//      val req = createIndex("docsAndTags").mappings(
//        mapping("my_type") templates(
//          dynamicTemplate("es", textField("") analyzer SpanishLanguageAnalyzer) matchPattern "regex" matching "*_es" matchMappingType "string",
//          dynamicTemplate("en", textField("") analyzer EnglishLanguageAnalyzer) matching "*" matchMappingType "string"
//          )
//      )
//      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/mappings_with_dyn_templates.json")
//    }
  }
}
