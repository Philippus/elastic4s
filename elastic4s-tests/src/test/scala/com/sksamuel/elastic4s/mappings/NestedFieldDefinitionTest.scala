package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.ElasticApi
import org.scalatest.{FlatSpec, Matchers}

class NestedFieldDefinitionTest extends FlatSpec with Matchers with ElasticApi {

  val field: NestedFieldDefinition = nestedField("myfield")

  "A NestedField" should "support boolean dynamic property" in {
    FieldBuilderFn(field.dynamic(true)).string() shouldBe
      """{"type":"nested","dynamic":"true"}"""
  }

  it should "support string dynamic property" in {
    FieldBuilderFn(field.dynamic("strict")).string() shouldBe
      """{"type":"nested","dynamic":"strict"}"""
  }

  it should "support include_in_root property" in {
    FieldBuilderFn(field.includeInRoot(true)).string() shouldBe
      """{"type":"nested","include_in_root":true}"""
  }

  it should "support include_in_parent property" in {
    FieldBuilderFn(field.includeInParent(true)).string() shouldBe
      """{"type":"nested","include_in_parent":true}"""
  }
}
