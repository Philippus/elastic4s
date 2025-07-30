package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.handlers.fields.{ElasticFieldBuilderFn, SparseVectorFieldBuilderFn}
import com.sksamuel.elastic4s.jackson.JacksonSupport
import com.sksamuel.elastic4s.requests.searches.queries.PruningConfig
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SparseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {
  "A SparseVectorField" should "be minimally supported" in {
    val field      = SparseVectorField("test.tokens")
    val jsonString = SparseVectorFieldBuilderFn.build(field).string
    jsonString shouldBe """{"type":"sparse_vector","store":false}"""
    ElasticFieldBuilderFn.construct(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](jsonString)
    ) shouldBe field
  }

  it should "support all fields" in {
    val sparseVectorIndexOptions =
      SparseVectorIndexOptions(prune = true, pruningConfig = Some(PruningConfig(Some(5), Some(1.0F))))

    val field      = SparseVectorField("test.tokens", store = true, indexOptions = Some(sparseVectorIndexOptions))
    val jsonString = SparseVectorFieldBuilderFn.build(field).string
    jsonString shouldBe """{"type":"sparse_vector","store":true,"index_options":{"prune":true,"pruning_config":{"tokens_freq_ratio_threshold":5,"tokens_weight_threshold":1.0}}}"""
    ElasticFieldBuilderFn.construct(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](jsonString)
    ) shouldBe field
  }
}
