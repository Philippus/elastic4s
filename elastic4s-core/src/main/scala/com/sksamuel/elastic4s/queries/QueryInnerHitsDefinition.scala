package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.search.HighlightDefinition

case class QueryInnerHitsDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val builder = new QueryInnerHitBuilder().setName(name)
  private var includes: Array[String] = Array.empty
  private var excludes: Array[String] = Array.empty

  def from(f: Int): this.type = {
    builder.setFrom(f)
    this
  }

  def size(s: Int): this.type = {
    builder.setSize(s)
    this
  }

  def highlighting(highlights: HighlightDefinition*): this.type = {
    highlights.foreach(highlight => builder.addHighlightedField(highlight.builder))
    this
  }

  def fetchSource(fetch: Boolean): this.type = {
    builder.setFetchSource(fetch)
    this
  }

  def sourceInclude(includes: String*): this.type = {
    this.includes = includes.toArray
    builder.setFetchSource(this.includes, excludes)
    this
  }

  def sourceExclude(excludes: String*): this.type = {
    this.excludes = excludes.toArray
    builder.setFetchSource(includes, this.excludes)
    this
  }
}
