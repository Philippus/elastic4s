package com.sksamuel.elastic4s.search

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
