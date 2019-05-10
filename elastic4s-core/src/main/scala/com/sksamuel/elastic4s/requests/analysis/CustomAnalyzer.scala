package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

/**
  * When the built-in analyzers do not fulfil your needs, you can create a custom analyzer
  * which uses the appropriate combination of:
  *
  * zero or more character filters
  * a tokenizer
  * zero or more token filters.
  *
  * Reference these by name, and if they are custom or configurable add them to the analysis definition.
  */
case class CustomAnalyzer(override val name: String,
                          tokenizer: String,
                          charFilters: List[String],
                          tokenFilters: List[String],
                          positionIncrementGap: Int = 100) extends Analyzer {
  override def build: XContentBuilder = CustomAnalyzerBuilder.build(this)
}

object CustomAnalyzerBuilder extends Builder[CustomAnalyzer] {
  override def build(a: CustomAnalyzer): XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "custom")
    b.field("tokenizer", a.tokenizer)
    if (a.tokenFilters.nonEmpty)
      b.array("filter", a.tokenFilters.toArray)
    if (a.charFilters.nonEmpty)
      b.array("char_filter", a.charFilters.toArray)
    b.endObject()
  }
}
