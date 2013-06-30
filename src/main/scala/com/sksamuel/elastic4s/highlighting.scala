package com.sksamuel.elastic4s

import org.elasticsearch.search.highlight.HighlightBuilder

/** @author Stephen Samuel */
trait HighlightDsl {
    def options = new HighlightOptionsDefinition
    class HighlightOptionsDefinition {
        var _preTags: Seq[String] = Nil
        var _postTags: Seq[String] = Nil
        var _encoder: Option[HighlightEncoder] = None
        var _order: Option[HighlightOrder] = None
        var _tagSchema: Option[TagSchema] = None
        var _requireFieldMatch: Boolean = false
        var _boundary_chars: Option[String] = None
        var _boundary_max_scan: Int = 20
    }
    def highlight = new HighlightExpectsField
    class HighlightExpectsField {
        def field(name: String) = new HighlightDefinition(name)
    }
    def highlight(field: String) = new HighlightDefinition(field)
}
sealed abstract class HighlightOrder
case object HighlightOrder {
    case object Score extends HighlightOrder
}

sealed abstract class TagSchema
case object TagSchema {
    case object Styled extends TagSchema
}

sealed abstract class HighlightEncoder
case object HighlightEncoder {
    case object Default extends HighlightEncoder
    case object Html extends HighlightEncoder
}

class HighlightDefinition(field: String) {

    val builder = new HighlightBuilder.Field(field)

    def size(s: Int) = fragmentSize(s)
    def fragmentSize(f: Int) = {
        builder.fragmentSize(f)
        this
    }

    def number(n: Int) = numberOfFragments(n)
    def numberOfFragments(n: Int) = {
        builder.numOfFragments(n)
        this
    }

    def offset(n: Int) = fragmentOffset(n)
    def fragmentOffset(n: Int) = {
        builder.fragmentOffset(n)
        this
    }

    def highlighterType(`type`: String) = {
        builder.highlighterType(`type`)
        this
    }
}