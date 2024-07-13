package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.{ElasticApi, JacksonSupport}
import com.sksamuel.elastic4s.fields.{Cosine, DenseVectorField, DotProduct, FlatIndexOptions, HnswIndexOptions, Int8FlatIndexOptions, Int8HnswIndexOptions, L2Norm, MaxInnerProduct}
import com.sksamuel.elastic4s.handlers.fields.{DenseVectorFieldBuilderFn, ElasticFieldBuilderFn}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "A DenseVectorField" should "support dims property" in {
    val field = DenseVectorField(name = "myfield", dims = 3)
    DenseVectorFieldBuilderFn.build(field).string shouldBe
      """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
  }

  it should "all similarity options" in {
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

  "A DenseVectorField" should "support a hnsw type of kNN algorithm for a index_options if a index property is true" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      index = true,
      similarity = L2Norm,
      indexOptions = Some(HnswIndexOptions(m = Some(100), efConstruction = Some(200)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm","index_options":{"type":"hnsw","m":100,"ef_construction":200}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "don't support a hnsw type of kNN algorithm for a index_options if a index property is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(HnswIndexOptions(m = Some(100), efConstruction = Some(200)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "support a int8_hnsw type of kNN algorithm for a index_options if a index property is true" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      index = true,
      similarity = L2Norm,
      indexOptions = Some(Int8HnswIndexOptions(m = Some(100), efConstruction = Some(200), confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm","index_options":{"type":"int8_hnsw","m":100,"ef_construction":200,"confidence_interval":0.5}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "don't support a int8_hnsw type of kNN algorithm for a index_options if a index property is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(Int8HnswIndexOptions(m = Some(100), efConstruction = Some(200), confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "support a flat type of kNN algorithm for a index_options if a index property is true" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      index = true,
      similarity = L2Norm,
      indexOptions = Some(FlatIndexOptions())
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm","index_options":{"type":"flat"}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "don't support a flat type of kNN algorithm for a index_options if a index property is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(FlatIndexOptions())
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
      indexOptions = Some(Int8FlatIndexOptions(confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":true,"similarity":"l2_norm","index_options":{"type":"int8_flat","confidence_interval":0.5}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }

  "A DenseVectorField" should "don't support a int8_flat type of kNN algorithm for a index_options if a index property  is false" in {
    val field = DenseVectorField(
      name = "myfield",
      dims = 3,
      similarity = L2Norm,
      indexOptions = Some(Int8FlatIndexOptions(confidenceInterval = Some(0.5d)))
    )
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
  }
}
