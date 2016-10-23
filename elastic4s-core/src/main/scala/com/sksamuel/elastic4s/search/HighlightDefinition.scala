package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.highlight.HighlightBuilder

case class HighlightDefinition(field: String) {

  val builder = new HighlightBuilder.Field(field)

  def boundaryChars(boundaryChars: String): this.type = {
    builder.boundaryChars(boundaryChars.toCharArray)
    this
  }

  def boundaryMaxScan(boundaryMaxScan: Int): this.type = {
    builder.boundaryMaxScan(boundaryMaxScan)
    this
  }

  def forceSource(forceSource: Boolean): this.type = {
    builder.forceSource(forceSource)
    this
  }

  def fragmenter(fragmenter: String): this.type = {
    builder.fragmenter(fragmenter)
    this
  }

  def fragmentOffset(n: Int): this.type = {
    builder.fragmentOffset(n)
    this
  }

  def fragmentSize(f: Int): this.type = {
    builder.fragmentSize(f)
    this
  }

  def highlightFilter(filter: Boolean): this.type = {
    builder.highlightFilter(filter)
    this
  }

  def highlighterType(`type`: String): this.type = {
    builder.highlighterType(`type`)
    this
  }

  def matchedFields(fields: String*): this.type = matchedFields(fields)
  def matchedFields(fields: Iterable[String]): this.type = {
    builder.matchedFields(fields.toSeq: _*)
    this
  }

  def noMatchSize(size: Int): this.type = {
    builder.noMatchSize(size)
    this
  }

  def numberOfFragments(n: Int): this.type = {
    builder.numOfFragments(n)
    this
  }

  def order(order: String): this.type = {
    builder.order(order)
    this
  }

  def query(query: QueryDefinition): this.type = {
    builder.highlightQuery(query.builder)
    this
  }

  def phraseLimit(limit: Int): this.type = {
    builder.phraseLimit(limit)
    this
  }

  def preTag(tags: String*): this.type = {
    builder.preTags(tags: _*)
    this
  }

  def postTag(tags: String*): this.type = {
    builder.postTags(tags: _*)
    this
  }

  def requireFieldMatchScan(requireFieldMatch: Boolean): this.type = {
    builder.requireFieldMatch(requireFieldMatch)
    this
  }
}
