package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analyzers.{EnglishLanguageAnalyzer, SpanishLanguageAnalyzer}
import com.sksamuel.elastic4s.indexes.CreateIndexContentBuilder
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.{Matchers, WordSpec}

class MappingDefinitionDslTest extends WordSpec with Matchers with JsonSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "mapping definition" should {
    "insert source exclusion directives when set" in {
      val mapping = MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2")
      val output = MappingContentBuilder.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
    }
    "insert source exclusion directives when set and override enabled directive" in {
      val mapping = MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2").source(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
      output should not include """"enabled":true"""
    }
    "insert source enabling" in {
      val mapping = MappingDefinition("test").source(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should not include """"_source":{"excludes":["excludeMe1","excludeMe2"]}"""
      output should include(""""enabled":false""")
    }
    "not insert date detection by default" in {
      val mapping = MappingDefinition("type")
      val output = MappingContentBuilder.build(mapping).string()
      output should not include "date"
    }
    "allow _id to be set to not_analyzed" in {
      val mapping = MappingDefinition("testy").id(IdField("not_analyzed"))
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""{"_id":{"index":"not_analyzed"}}""")
    }
    "insert date detection when set to true" in {
      val mapping = MappingDefinition("type").dateDetection(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""date_detection":true""")
    }
    "insert date detection when set to false" in {
      val mapping = MappingDefinition("type").dateDetection(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""date_detection":false""")
    }
    "not insert numeric detection by default" in {
      val mapping = MappingDefinition("type")
      val output = MappingContentBuilder.build(mapping).string()
      output should not include "numeric"
    }
    "insert numeric detection when set to true" in {
      val mapping = MappingDefinition("type").numericDetection(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""numeric_detection":true""")
    }
    "insert numeric detection when set to false" in {
      val mapping = MappingDefinition("type").numericDetection(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""numeric_detection":false""")
    }
    "include dynamic templates" in {
      val req = createIndex("docsAndTags").mappings(
        mapping("my_type") templates(
          dynamicTemplate("es", field typed StringType analyzer SpanishLanguageAnalyzer) matchPattern "regex" matching "*_es" matchMappingType "string",
          dynamicTemplate("en", field typed StringType analyzer EnglishLanguageAnalyzer) matching "*" matchMappingType "string"
          )
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/mappings_with_dyn_templates.json")
    }
    "include timestamp path field" in {
      val req = createIndex("docsAndTags").mappings(
        mapping ("foo") timestamp {
          timestamp enabled true path "mypath" store true
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_path.json")
    }
    "include timestamp store field" in {
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(true) path "mypath" store true
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_store.json")
    }
    "include timestamp format field" in {
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(true) format "qwerty"
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_format.json")
    }
    "include honor disabled timestamp" in {
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(false)
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_disabled.json")
    }
  }
}
