package com.sksamuel.elastic4s.requests.index

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.analysis.{Analysis, CustomNormalizer}
import org.scalatest.{FunSuite, Matchers}

class CreateIndexTemplateRequestTest extends FunSuite with ElasticDsl with Matchers {

  test("testing if entity created by createIndexTemplate has proper form"){

    val lowerCaseNormalizer = CustomNormalizer("lowercase", Nil, List("lowercase"))

    val templateName = "test_template"

    val templateDef = createIndexTemplate(templateName, "index_pattern")
      .analysis(Analysis(Nil, normalizers = List(lowerCaseNormalizer)))

    val expectedEntityContent = """{"index_patterns":["index_pattern"],"settings":{"analysis":{"normalizer":{"lowercase":{"type":"custom","filter":["lowercase"]}}}}}"""

    CreateIndexTemplateHandler.build(templateDef).entity.get.get shouldBe expectedEntityContent
  }
}
