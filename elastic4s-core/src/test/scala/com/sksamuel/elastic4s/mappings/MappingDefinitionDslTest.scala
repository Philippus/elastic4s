package com.sksamuel.elastic4s.mappings

import java.io.{StringWriter, Writer}

import com.commodityvectors.snapshotmatchers.SnapshotMatcher
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import com.sksamuel.elastic4s.analyzers.{EnglishLanguageAnalyzer, SpanishLanguageAnalyzer}
import com.sksamuel.elastic4s.indexes.CreateIndexContentBuilder
import org.elasticsearch.common.xcontent.XContentBuilder
import org.scalatest.{Matchers, WordSpec, fixture}

class MappingDefinitionDslTest extends fixture.WordSpec with Matchers with JsonSugar with ElasticApi with SnapshotMatcher {

  "mapping definition" should {
    "insert source exclusion directives when set" in { implicit test =>
      val mapping = MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2")
      val output = MappingContentBuilder.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
    }
    "insert source exclusion directives when set and override enabled directive" in { implicit test =>
      val mapping = MappingDefinition("test").sourceExcludes("excludeMe1", "excludeMe2").source(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include(""""_source":{"excludes":["excludeMe1","excludeMe2"]}""")
      output should not include """"enabled":true"""
    }
    "insert source enabling" in { implicit test =>
      val mapping = MappingDefinition("test").source(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should not include """"_source":{"excludes":["excludeMe1","excludeMe2"]}"""
      output should include(""""enabled":false""")
    }
    "not insert date detection by default" in { implicit test =>
      val mapping = MappingDefinition("type")
      val output = MappingContentBuilder.build(mapping).string()
      output should not include "date"
    }
    "insert date detection when set to true" in { implicit test =>
      val mapping = MappingDefinition("type").dateDetection(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""date_detection":true""")
    }
    "insert date detection when set to false" in { implicit test =>
      val mapping = MappingDefinition("type").dateDetection(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""date_detection":false""")
    }
    "not insert numeric detection by default" in { implicit test =>
      val mapping = MappingDefinition("type")
      val output = MappingContentBuilder.build(mapping).string()
      output should not include "numeric"
    }
    "insert numeric detection when set to true" in { implicit test =>
      val mapping = MappingDefinition("type").numericDetection(true)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""numeric_detection":true""")
    }
    "insert numeric detection when set to false" in { implicit test =>
      val mapping = MappingDefinition("type").numericDetection(false)
      val output = MappingContentBuilder.build(mapping).string()
      output should include("""numeric_detection":false""")
    }
    "include dynamic templates" in { implicit test =>
      val req = createIndex("docsAndTags").mappings(
        mapping("my_type") templates(
          dynamicTemplate("es", textField("") analyzer SpanishLanguageAnalyzer) matchPattern "regex" matching "*_es" matchMappingType "string",
          dynamicTemplate("en", textField("") analyzer EnglishLanguageAnalyzer) matching "*" matchMappingType "string"
          )
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/mappings_with_dyn_templates.json")
    }
    "include timestamp path field" in { implicit test =>
      val req = createIndex("docsAndTags").mappings(
        mapping ("foo") timestamp {
          timestamp(true) path "mypath" store true
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_path.json")
    }
    "include timestamp store field" in { implicit test =>
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(true) path "mypath" store true
        }
      )

      CreateIndexContentBuilder(req) should matchSnapshot[XContentBuilder]()
    }
    "include timestamp format field" in { implicit test =>
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(true) format "qwerty"
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_format.json")
    }
    "include honor disabled timestamp" in { implicit test =>
      val req = createIndex("docsAndTags").mappings(
        mapping("foo") timestamp {
          timestamp(false)
        }
      )
      CreateIndexContentBuilder(req).string() should matchJsonResource("/json/mappings/timestamp_disabled.json")
    }
  }
}
