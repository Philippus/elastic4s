package com.sksamuel.elastic4s

import org.elasticsearch.search.highlight.HighlightBuilder

import scala.language.implicitConversions

/** @author Stephen Samuel */
trait HighlightDsl {

  implicit def string2highlightfield(name: String): HighlightDefinition = new HighlightDefinition(name)

  case object highlight {
    def field(name: String) = new HighlightDefinition(name)
  }
  def highlight(field: String) = new HighlightDefinition(field)

  def options = new HighlightOptionsDefinition
}

class HighlightOptionsDefinition {

  var _preTags: Seq[String] = Nil
  var _postTags: Seq[String] = Nil
  var _encoder: Option[HighlightEncoder] = None
  var _order: Option[HighlightOrder] = None
  var _tagSchema: Option[TagSchema] = None
  var _requireFieldMatch: Boolean = false
  var _boundary_chars: Option[String] = None
  var _boundary_max_scan: Int = 20

  def boundaryMaxScan(max: Int): this.type = {
    _boundary_max_scan = max
    this
  }

  def boundaryChars(chars: String): this.type = {
    _boundary_chars = Option(chars)
    this
  }

  def requireFieldMatch(requireFieldMatch: Boolean): this.type = {
    _requireFieldMatch = requireFieldMatch
    this
  }

  def tagSchema(tagSchema: TagSchema): this.type = {
    _tagSchema = Option(tagSchema)
    this
  }

  def order(order: HighlightOrder): this.type = {
    _order = Option(order)
    this
  }

  def encoder(encoder: HighlightEncoder): this.type = {
    this._encoder = Option(encoder)
    this
  }

  def postTags(iterable: Iterable[String]): this.type = postTags(iterable.toSeq: _*)
  def postTags(tags: String*): this.type = {
    this._postTags = tags
    this
  }

  def preTags(iterable: Iterable[String]): this.type = preTags(iterable.toSeq: _*)
  def preTags(tags: String*): this.type = {
    this._preTags = tags
    this
  }
}

abstract class HighlightOrder(val elastic: String)
object HighlightOrder {
  case object Score extends HighlightOrder("score")
}

abstract class TagSchema(val elastic: String)
object TagSchema {
  case object Styled extends TagSchema("styled")
}

abstract class HighlightEncoder(val elastic: String)
object HighlightEncoder {
  case object Default extends HighlightEncoder("default")
  case object Html extends HighlightEncoder("html")
}

class HighlightDefinition(field: String) {

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
