package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.fields.DenseVectorField.{Flat, Hnsw, Int8Flat, Int8Hnsw}
import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.fields.{Cosine, DenseVectorField, DenseVectorIndexOptions, DotProduct, L2Norm, MaxInnerProduct}
import com.sksamuel.elastic4s.handlers.fields.{DenseVectorFieldBuilderFn}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {
  private val denseVectorIndexOptions = DenseVectorIndexOptions(Int8Hnsw, Some(10), Some(100), Some(1.0f))

  "A DenseVectorField" should "support dims property" in {
    val field = DenseVectorField(name = "myfield").dims(3)
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3}"""
  }

  it should "support all similarity options" in {
    val field = DenseVectorField(name = "myfield").dims(3).index(true).similarity(L2Norm)
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
    val field = DenseVectorField(name = "myfield").dims(3).elementType("byte")
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","element_type":"byte","dims":3}"""
  }

  it should "not set similarity or indexOptions when index = false" in {
    val field = DenseVectorField(name = "myfield", dims = Some(3), index = Some(false), indexOptions = Some(denseVectorIndexOptions))
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":false}"""
  }

  it should "support indexOptions property" in {
    val field = DenseVectorField(name = "myfield", dims = Some(3), index = Some(true), indexOptions = Some(denseVectorIndexOptions))
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"index_options":{"type":"int8_hnsw","m":10,"ef_construction":100,"confidence_interval":1.0}}"""
  }

  it should "support all index options types and only set m, efConstruction and confidenceInterval when applicable" in {
    val field = DenseVectorField(name = "myfield", dims = Some(3), index = Some(true), indexOptions = Some(denseVectorIndexOptions))
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"index_options":{"type":"int8_hnsw","m":10,"ef_construction":100,"confidence_interval":1.0}}"""
    DenseVectorFieldBuilderFn.build(field.indexOptions(denseVectorIndexOptions.copy(`type` = Hnsw))).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"index_options":{"type":"hnsw","m":10,"ef_construction":100}}"""
    DenseVectorFieldBuilderFn.build(field.indexOptions(denseVectorIndexOptions.copy(`type` = Flat))).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"index_options":{"type":"flat"}}"""
    DenseVectorFieldBuilderFn.build(field.indexOptions(denseVectorIndexOptions.copy(`type` = Int8Flat))).string shouldBe
      """{"type":"dense_vector","dims":3,"index":true,"index_options":{"type":"int8_flat","confidence_interval":1.0}}"""
  }
}
