package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.fields.DenseVectorField
import com.sksamuel.elastic4s.handlers.fields.DenseVectorFieldBuilderFn
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  private val field = DenseVectorField(name = "myfield", dims = 3)

  "A DenseVectorField" should "support dims property" in {
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
  }
}
