package com.sksamuel.elastic4s2.admin

import org.scalatest.WordSpec

class CreateIndexTemplateDefinitionTest extends WordSpec {

  import com.sksamuel.elastic4s2.ElasticDsl._

  val req = createTemplate("my_template").pattern("matchme.*").mappings(
    mapping("sometype1").fields(
      stringField("field1"),
      geopointField("field2")
    ),
    mapping("sometype2").fields(
      stringField("field3"),
      intField("field4")
    )
    )
}
