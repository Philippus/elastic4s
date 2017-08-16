package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, KeywordTokenizer, LowercaseTokenFilter}
import com.sksamuel.elastic4s.http.ElasticDsl
import org.scalatest.{Matchers, WordSpec}

class CreateIndexTemplateDefinitionShowTest extends WordSpec with Matchers with JsonSugar with ElasticDsl {

  "CreateIndexTemplateDefinition" should {
    "have a show typeclass implementation" in {
      val req =
        createTemplate("matchme.*").pattern("matchme.*").mappings(
          mapping("characters").fields(
            stringField("name"),
            stringField("location")
          )
        ).analysis(
          CustomAnalyzerDefinition(
            "default",
            KeywordTokenizer,
            LowercaseTokenFilter
          )
        )
        .order(1)
        .settings(Map("number_of_shards" -> 4))

      CreateIndexTemplateShow.show(req) should matchJson("""{"template":"matchme.*","order":1,"settings":{"number_of_shards":4,"analysis":{"analyzer":{"default":{"type":"custom","tokenizer":"keyword","filter":["lowercase"]}}}},"mappings":{"characters":{"properties":{"name":{"type":"string"},"location":{"type":"string"}}}}}""")
    }
  }
}
