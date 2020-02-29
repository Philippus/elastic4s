package com.sksamuel.elastic4s.requests.index

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.analysis.{Analysis, CustomNormalizer}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CreateIndexTemplateRequestTest extends AnyFunSuite with ElasticDsl with Matchers {

  test("testing if entity created by createIndexTemplate has proper form"){

    val lowerCaseNormalizer = CustomNormalizer("lowercase", Nil, List("lowercase"))

    val templateName = "test_template"

    val templateDef = createIndexTemplate(templateName, "index_pattern")
      .analysis(Analysis(Nil, normalizers = List(lowerCaseNormalizer)))

    val expectedEntityContent = """{"index_patterns":["index_pattern"],"settings":{"analysis":{"normalizer":{"lowercase":{"type":"custom","filter":["lowercase"]}}}}}"""

    CreateIndexTemplateHandler.build(templateDef).entity.get.get shouldBe expectedEntityContent
  }

  test("testing if entity created by createIndexTemplate without analysis"){

    val templateName = "test_template_without_analysis"

    val templateDef = createIndexTemplate(templateName, "index_pattern").settings(Map("number_of_shards" -> 1))

    val expectedEntityContent = """{"index_patterns":["index_pattern"],"settings":{"number_of_shards":1}}"""

    CreateIndexTemplateHandler.build(templateDef).entity.get.get shouldBe expectedEntityContent
  }
}
