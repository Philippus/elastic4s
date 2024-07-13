package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DenseVectorField.{Hnsw, Int8Flat, Int8Hnsw}
import com.sksamuel.elastic4s.fields.{DenseVectorField, DenseVectorIndexOptions}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {

  private def getIndexOptions(values: Map[String, Any]): DenseVectorIndexOptions =
    values("type").asInstanceOf[String] match {
      case "hnsw" => DenseVectorIndexOptions(
        DenseVectorField.Hnsw,
        values.get("m").map(_.asInstanceOf[Int]),
        values.get("ef_construction").map(_.asInstanceOf[Int])
      )
      case "int8_hnsw" => DenseVectorIndexOptions(
        DenseVectorField.Int8Hnsw,
        values.get("m").map(_.asInstanceOf[Int]),
        values.get("ef_construction").map(_.asInstanceOf[Int]),
        values.get("confidence_interval").map(_.asInstanceOf[Double])
      )
      case "flat" => DenseVectorIndexOptions(
        DenseVectorField.Flat
      )
      case "int8_flat" => DenseVectorIndexOptions(
        DenseVectorField.Int8Flat,
        None,
        None,
        values.get("confidence_interval").map(_.asInstanceOf[Double])
      )
    }

  def toField(name: String, values: Map[String, Any]): DenseVectorField = DenseVectorField(
    name,
    values("dims").asInstanceOf[Int],
    values("index").asInstanceOf[Boolean],
    indexOptions = values.get("index_options").map(_.asInstanceOf[Map[String, Any]]).map(getIndexOptions),
    elementType = values.get("element_type").map(_.asInstanceOf[String])
  )

  def build(field: DenseVectorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.elementType.foreach(builder.field("element_type", _))
    builder.field("dims", field.dims)
    builder.field("index", field.index)
    builder.field("similarity", field.similarity.name)
    if (field.index)
      field.indexOptions.foreach { options =>
        builder.startObject("index_options")
        builder.field("type", options.`type`.name)
        if (Seq(Hnsw, Int8Hnsw).contains(options.`type`)) options.m.foreach(builder.field("m", _))
        if (Seq(Hnsw, Int8Hnsw).contains(options.`type`)) options.efConstruction.foreach(builder.field("ef_construction", _))
        if (Seq(Int8Hnsw, Int8Flat).contains(options.`type`)) options.confidenceInterval.foreach(builder.field("confidence_interval", _))
        builder.endObject()
      }
    builder.endObject()
  }
}
