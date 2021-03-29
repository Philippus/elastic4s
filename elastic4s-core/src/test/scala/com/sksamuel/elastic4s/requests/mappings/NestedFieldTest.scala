package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.fields.NestedField
import com.sksamuel.elastic4s.handlers.fields.NestedFieldBuilderFn
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NestedFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  private val field = NestedField("myfield")

  "A NestedField" should "support boolean dynamic property" in {
    NestedFieldBuilderFn.build(field.dynamic(true)).string() shouldBe
      """{"type":"nested","dynamic":"true"}"""
  }

  it should "support string dynamic property" in {
    NestedFieldBuilderFn.build(field.dynamic("strict")).string() shouldBe
      """{"type":"nested","dynamic":"strict"}"""
  }

  it should "support include_in_root property" in {
    NestedFieldBuilderFn.build(field.includeInRoot(true)).string() shouldBe
      """{"type":"nested","include_in_root":true}"""
  }

  it should "support include_in_parent property" in {
    NestedFieldBuilderFn.build(field.includeInParent(true)).string() shouldBe
      """{"type":"nested","include_in_parent":true}"""
  }
}
