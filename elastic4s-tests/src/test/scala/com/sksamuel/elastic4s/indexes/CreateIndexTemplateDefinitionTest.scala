package com.sksamuel.elastic4s.indexes

import org.scalatest.WordSpec

class CreateIndexTemplateDefinitionTest extends WordSpec {

  import com.sksamuel.elastic4s.ElasticDsl._

  private val req = createTemplate("my_template").pattern("matchme.*").mappings(
    mapping("sometype1").fields(
      keywordField("field1"),
      geopointField("field2")
    ),
    mapping("sometype2").fields(
      keywordField("field3"),
      intField("field4")
    )
  )
}
