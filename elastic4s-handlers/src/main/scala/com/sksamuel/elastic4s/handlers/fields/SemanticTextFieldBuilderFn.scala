package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{ChunkingSettings, SemanticTextField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object SemanticTextFieldBuilderFn {
  private def getChunkingSettings(values: Map[String, Any]): ChunkingSettings =
    values("type").asInstanceOf[String] match {
      case "word"     => ChunkingSettings.word(
          values.get("max_chunk_size").map(_.asInstanceOf[Int]).get,
          values.get("overlap").map(_.asInstanceOf[Int]).get
        )
      case "sentence" => ChunkingSettings.sentence(
          values.get("max_chunk_size").map(_.asInstanceOf[Int]).get,
          values.get("sentence_overlap").map(_.asInstanceOf[Int]).get
        )
    }

  def toField(name: String, values: Map[String, Any]): SemanticTextField = SemanticTextField(
    name,
    values.get("inference_id").map(_.asInstanceOf[String]).get,
    values.get("search_inference_id").map(_.asInstanceOf[String]),
    values.get("chunking_settings").map(_.asInstanceOf[Map[String, Any]]).map(getChunkingSettings)
  )

  def build(field: SemanticTextField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("inference_id", field.inferenceId)
    field.searchInferenceId.foreach(builder.field("search_inference_id", _))
    field.chunkingSettings.foreach { chunkingSettings =>
      builder.startObject("chunking_settings")
      builder.field("type", chunkingSettings.`type`)
      builder.field("max_chunk_size", chunkingSettings.maxChunkSize)
      chunkingSettings.`type` match {
        case "word"     => chunkingSettings.overlap.foreach(builder.field("overlap", _))
        case "sentence" => chunkingSettings.sentenceOverlap.foreach(builder.field("sentence_overlap", _))
      }
      builder.endObject()
    }
    builder.endObject()
  }
}
