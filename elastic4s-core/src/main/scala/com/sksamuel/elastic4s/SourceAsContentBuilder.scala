package com.sksamuel.elastic4s

object SourceAsContentBuilder {

  def apply(source: Map[String, Any]): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    source.foreach((builder.autofield _).tupled)
    builder
  }
}
