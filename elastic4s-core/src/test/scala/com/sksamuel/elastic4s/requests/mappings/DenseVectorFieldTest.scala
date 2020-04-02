package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.ElasticApi
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  private val field = DenseVectorField(name = "myfield", dims = 3)

  "A DenseVectorField" should "support dims property" in {
    FieldBuilderFn(field).string() shouldBe
      """{"type":"dense_vector","dims":3}"""
  }
}
