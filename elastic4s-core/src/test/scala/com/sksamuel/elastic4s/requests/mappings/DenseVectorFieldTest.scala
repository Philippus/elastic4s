package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.{ElasticApi, JacksonSupport}
import com.sksamuel.elastic4s.fields.{DenseVectorField, DenseVectorIndexOptions, Hnsw}
import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DenseVectorFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  private val field = DenseVectorField(
    name = "myfield",
    dims = 3,
    indexOptions = Some(DenseVectorIndexOptions(
      `type` = Hnsw,
      m = Some(100),
      efConstruction = Some(100),
      confidenceInterval = Some(0.5d)
    ))
  )

  "A DenseVectorField" should "support dims and index_options properties" in {
    val jsonStringValue = """{"type":"dense_vector","dims":3,"index":false,"similarity":"l2_norm","index_options":{"type":"hnsw","m":100,"ef_construction":100,"confidence_interval":0.5}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
    ElasticFieldBuilderFn.construct(field.name, JacksonSupport.mapper.readValue[Map[String, Any]](jsonStringValue)) shouldBe (field)
  }
}
