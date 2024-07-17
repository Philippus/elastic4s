package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DenseVectorField.{Hnsw, Int4Flat, Int4Hnsw, Int8Flat, Int8Hnsw}
import com.sksamuel.elastic4s.fields.{Cosine, DenseVectorField, DenseVectorIndexOptions, DotProduct, L2Norm, MaxInnerProduct, Similarity}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {
  private def similarityFromString(similarity: String): Similarity = similarity match {
    case "l2_norm" => L2Norm
    case "dot_product" => DotProduct
    case "cosine" => Cosine
    case "max_inner_product" => MaxInnerProduct
  }

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
        values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
      )
      case "int4_hnsw" => DenseVectorIndexOptions(
        DenseVectorField.Int4Hnsw,
        values.get("m").map(_.asInstanceOf[Int]),
        values.get("ef_construction").map(_.asInstanceOf[Int]),
        values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
      )
      case DenseVectorField.Flat.name => DenseVectorIndexOptions(
        DenseVectorField.Flat
      )
      case "int8_flat" => DenseVectorIndexOptions(
        DenseVectorField.Int8Flat,
        None,
        None,
        values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
      )
      case "int4_flat" => DenseVectorIndexOptions(
        DenseVectorField.Int4Flat,
        None,
        None,
        values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
      )
    }

  def toField(name: String, values: Map[String, Any]): DenseVectorField = DenseVectorField(
    name,
    values.get("element_type").map(_.asInstanceOf[String]),
    values.get("dims").map(_.asInstanceOf[Int]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("similarity").map(s => similarityFromString(s.asInstanceOf[String])),
    indexOptions = values.get("index_options").map(_.asInstanceOf[Map[String, Any]]).map(getIndexOptions),
  )

  def build(field: DenseVectorField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.elementType.foreach(builder.field("element_type", _))
    field.dims.foreach(builder.field("dims", _))
    field.index.foreach(builder.field("index", _))
    if (field.index.getOrElse(true)) {
      field.similarity.foreach(similarity => builder.field("similarity", similarity.name))
      field.indexOptions.foreach { options =>
        builder.startObject("index_options")
        builder.field("type", options.`type`.name)
        if (Seq(Hnsw, Int8Hnsw, Int4Hnsw).contains(options.`type`)) options.m.foreach(builder.field("m", _))
        if (Seq(Hnsw, Int8Hnsw, Int4Hnsw).contains(options.`type`)) options.efConstruction.foreach(builder.field("ef_construction", _))
        if (Seq(Int8Hnsw, Int4Hnsw, Int8Flat, Int4Flat).contains(options.`type`)) options.confidenceInterval.foreach(builder.field("confidence_interval", _))
        builder.endObject()
      }
    }
    builder.endObject()
  }
}
