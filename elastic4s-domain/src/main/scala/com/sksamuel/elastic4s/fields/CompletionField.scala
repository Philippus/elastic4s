package com.sksamuel.elastic4s.fields

case class CompletionField(name: String,
                           analyzer: Option[String] = None,
                           boost: Option[Double] = None,
                           copyTo: Seq[String] = Nil,
                           index: Option[Boolean] = None,
                           indexOptions: Option[String] = None,
                           ignoreAbove: Option[Int] = None,
                           ignoreMalformed: Option[Boolean] = None,
                           maxInputLength: Option[Int] = None,
                           norms: Option[Boolean] = None,
                           nullValue: Option[String] = None,
                           preserveSeparators: Option[Boolean] = None,
                           preservePositionIncrements: Option[Boolean] = None,
                           similarity: Option[String] = None,
                           searchAnalyzer: Option[String] = None,
                           store: Option[Boolean] = None,
                           termVector: Option[String] = None,
                           contexts: Seq[ContextField] = Nil,
                           meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "completion"
}
