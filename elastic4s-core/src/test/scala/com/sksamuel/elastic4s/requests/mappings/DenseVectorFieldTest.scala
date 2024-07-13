package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.fields.DenseVectorField.{Hnsw, Int8Hnsw}
import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.fields.{Cosine, DenseVectorField, DenseVectorIndexOptions, DotProduct, L2Norm, MaxInnerProduct}
import com.sksamuel.elastic4s.handlers.fields.{DenseVectorFieldBuilderFn, ElasticFieldBuilderFn}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "A DenseVectorField" should "support dims property" in {
    val field = DenseVectorField(name = "myfield", dims = 3)
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":false}"""
  }

  it should "support all similarity options" in {
    val field = DenseVectorField(name = "myfield", dims = 3, index = true, similarity = L2Norm)
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm"}"""
    DenseVectorFieldBuilderFn.build(field.similarity(DotProduct)).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"similarity":"dot_product"}"""
    DenseVectorFieldBuilderFn.build(field.similarity(Cosine)).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"similarity":"cosine"}"""
    DenseVectorFieldBuilderFn.build(field.similarity(MaxInnerProduct)).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"similarity":"max_inner_product"}"""
  }

  it should "support elementType property" in {
    val field = DenseVectorField(name = "myfield", dims = 3).elementType("byte")
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","element_type":"byte","dims":3,"index":false}"""
  }

  it should "not set similarity or indexOptions when index = false" in {
    val field = DenseVectorField(name = "myfield", dims = 3, index = false, indexOptions = Some(denseVectorIndexOptions))
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":false}"""
  }

  "A DenseVectorField" should "don't support a flat type of kNN algorithm for a index_options if a index property is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(DenseVectorIndexOptions(`type` = DenseVectorField.Flat))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "support a int8_flat type of kNN algorithm for a index_options if a index property is true" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      index = true,
      similarity = L2Norm,
      indexOptions = Some(DenseVectorIndexOptions(`type` = DenseVectorField.Int8Flat, confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm","index_options":{"type":"int8_flat","confidence_interval":0.5}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "don't support a int8_flat type of kNN algorithm for a index_options if a index property  is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(DenseVectorIndexOptions(`type` = DenseVectorField.Int8Flat, confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }
}
