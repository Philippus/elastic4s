package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.json.XContentBuilder

abstract class LanguageAnalyzerDef(override val name: String,
                                   stopwords: Iterable[String] = Nil) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    source.field("lang", name)
    source.endObject()
  }
}
