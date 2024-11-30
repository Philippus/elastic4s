package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DenseVectorField.{BbqHnsw, Hnsw, Int4Flat, Int4Hnsw, Int8Flat, Int8Hnsw}
import com.sksamuel.elastic4s.fields.{
  Cosine,
  DenseVectorField,
  DenseVectorIndexOptions,
  DotProduct,
  L2Norm,
  MaxInnerProduct,
  Similarity
}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {
  private def similarityFromString(similarity: String): Similarity = similarity match {
    case L2Norm.name          => L2Norm
    case DotProduct.name      => DotProduct
    case Cosine.name          => Cosine
    case MaxInnerProduct.name => MaxInnerProduct
  }

  private def getIndexOptions(values: Map[String, Any]): DenseVectorIndexOptions =
    values("type").asInstanceOf[String] match {
      case DenseVectorField.Hnsw.name     => DenseVectorIndexOptions(
          DenseVectorField.Hnsw,
          values.get("m").map(_.asInstanceOf[Int]),
          values.get("ef_construction").map(_.asInstanceOf[Int])
        )
      case DenseVectorField.Int8Hnsw.name => DenseVectorIndexOptions(
          DenseVectorField.Int8Hnsw,
          values.get("m").map(_.asInstanceOf[Int]),
          values.get("ef_construction").map(_.asInstanceOf[Int]),
          values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
        )
      case DenseVectorField.Int4Hnsw.name => DenseVectorIndexOptions(
          DenseVectorField.Int4Hnsw,
          values.get("m").map(_.asInstanceOf[Int]),
          values.get("ef_construction").map(_.asInstanceOf[Int]),
          values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
        )
      case DenseVectorField.BbqHnsw.name  => DenseVectorIndexOptions(
          DenseVectorField.BbqHnsw,
          values.get("m").map(_.asInstanceOf[Int]),
          values.get("ef_construction").map(_.asInstanceOf[Int])
        )
      case DenseVectorField.Flat.name     => DenseVectorIndexOptions(
          DenseVectorField.Flat
        )
      case DenseVectorField.Int8Flat.name => DenseVectorIndexOptions(
          DenseVectorField.Int8Flat,
          None,
          None,
          values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
        )
      case DenseVectorField.Int4Flat.name => DenseVectorIndexOptions(
          DenseVectorField.Int4Flat,
          None,
          None,
          values.get("confidence_interval").map(d => d.asInstanceOf[Double].toFloat)
        )
      case DenseVectorField.BbqFlat.name  => DenseVectorIndexOptions(
          DenseVectorField.BbqFlat
        )
    }

  def toField(name: String, values: Map[String, Any]): DenseVectorField = DenseVectorField(
    name,
    values.get("element_type").map(_.asInstanceOf[String]),
    values.get("dims").map(_.asInstanceOf[Int]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("similarity").map(s => similarityFromString(s.asInstanceOf[String])),
    indexOptions = values.get("index_options").map(_.asInstanceOf[Map[String, Any]]).map(getIndexOptions)
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
        if (Seq(Hnsw, Int8Hnsw, Int4Hnsw, BbqHnsw).contains(options.`type`)) options.m.foreach(builder.field("m", _))
        if (Seq(Hnsw, Int8Hnsw, Int4Hnsw, BbqHnsw).contains(options.`type`))
          options.efConstruction.foreach(builder.field("ef_construction", _))
        if (Seq(Int8Hnsw, Int4Hnsw, Int8Flat, Int4Flat).contains(options.`type`))
          options.confidenceInterval.foreach(builder.field("confidence_interval", _))
        builder.endObject()
      }
    }
    builder.endObject()
  }
}
