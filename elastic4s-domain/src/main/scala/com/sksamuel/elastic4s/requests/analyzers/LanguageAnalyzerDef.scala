package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.json.XContentBuilder

@deprecated("use new analysis package", "7.0.1")
abstract class LanguageAnalyzerDef(override val name: String, stopwords: Iterable[String] = Nil)
    extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    source.field("lang", name)
    source.endObject()
  }
}
