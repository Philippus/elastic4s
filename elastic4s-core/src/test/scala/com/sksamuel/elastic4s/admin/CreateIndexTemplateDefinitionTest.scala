package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.WordSpec

class CreateIndexTemplateDefinitionTest extends WordSpec {

  import ElasticDsl._

  val req = create template "my_template" pattern "matchme.*" mappings(
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
