package com.sksamuel.elastic4s.fields

case class ChunkingSettings(
    strategy: String,
    maxChunkSize: Option[Int] = None,
    overlap: Option[Int] = None,
    sentenceOverlap: Option[Int] = None
)

object ChunkingSettings {
  def none(): ChunkingSettings                                            = ChunkingSettings(strategy = "none")
  def sentence(maxChunkSize: Int, sentenceOverlap: Int): ChunkingSettings =
    ChunkingSettings(strategy = "sentence", maxChunkSize = Some(maxChunkSize), sentenceOverlap = Some(sentenceOverlap))
  def word(maxChunkSize: Int, overlap: Int): ChunkingSettings             =
    ChunkingSettings(strategy = "word", maxChunkSize = Some(maxChunkSize), overlap = Some(overlap))
}

object SemanticTextField {
  val `type`: String = "semantic_text"
}

case class SemanticTextField(
    override val name: String,
    inferenceId: String,
    searchInferenceId: Option[String] = None,
    chunkingSettings: Option[ChunkingSettings] = None
) extends ElasticField {
  override def `type`: String = SemanticTextField.`type`

  def chunkingSettings(chunkingSettings: ChunkingSettings): SemanticTextField =
    copy(chunkingSettings = Some(chunkingSettings))
}
