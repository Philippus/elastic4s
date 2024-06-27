package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{DenseVectorField, DenseVectorIndexOptions, Flat, Hnsw, Int8Flat, Int8Hnsw}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {

  private def getIndexOptions(values: Map[String, Any]) = DenseVectorIndexOptions(
    values("type").asInstanceOf[String] match {
      case "hnsw" => Hnsw
      case "int8_hnsw" => Int8Hnsw
      case "flat" => Flat
      case "int8_flat" => Int8Flat
    },
    values.get("m").map(_.asInstanceOf[Int]),
    values.get("ef_construction").map(_.asInstanceOf[Int]),
    values.get("confidence_interval").map(_.asInstanceOf[Double])
  )

  def toField(name: String, values: Map[String, Any]): DenseVectorField = DenseVectorField(
    name,
    values.get("dims").map(_.asInstanceOf[Int]).get,
    indexOptions = values.get("index_options").map(_.asInstanceOf[Map[String, Any]]).map(getIndexOptions)
  )


  def build(field: DenseVectorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("dims", field.dims)
    builder.field("index", field.index)
    builder.field("similarity", field.similarity.name)
    field.indexOptions.foreach { options =>
      builder.startObject("index_options")
      builder.field("type", options.`type`.name)
      options.m.foreach(builder.field("m", _))
      options.efConstruction.foreach(builder.field("ef_construction", _))
      options.confidenceInterval.foreach(builder.field("confidence_interval", _))
      builder.endObject()
    }
    builder.endObject()
  }
}
