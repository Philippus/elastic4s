package com.sksamuel.elastic4s

import org.elasticsearch.search.highlight.HighlightBuilder

/** @author Stephen Samuel */
trait HighlightDsl {

    implicit def string2highlightfield(name: String) = new HighlightDefinition(name)

    def highlight = new HighlightExpectsField
    class HighlightExpectsField {
        def field(name: String) = new HighlightDefinition(name)
    }
    def highlight(field: String) = new HighlightDefinition(field)

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

        def boundaryMaxScan(max: Int) = {
            _boundary_max_scan = max
            this
        }
        def boundaryChars(chars: String) = {
            _boundary_chars = Option(chars)
            this
        }
        def requireFieldMatch(requireFieldMatch: Boolean) = {
            _requireFieldMatch = requireFieldMatch
            this
        }
        def tagSchema(tagSchema: TagSchema) = {
            _tagSchema = Option(tagSchema)
            this
        }
        def order(order: HighlightOrder) = {
            _order = Option(order)
            this
        }
        def encoder(encoder: HighlightEncoder) = {
            this._encoder = Option(encoder)
            this
        }
        def postTags(tags: String*) = {
            this._postTags = tags
            this
        }
        def preTags(tags: String*) = {
            this._preTags = tags
            this
        }
    }

}
abstract class HighlightOrder(val elastic: String)
case object HighlightOrder {
    case object Score extends HighlightOrder("score")
}

abstract class TagSchema(val elastic: String)
case object TagSchema {
    case object Styled extends TagSchema("styled")
}

abstract class HighlightEncoder(val elastic: String)
case object HighlightEncoder {
    case object Default extends HighlightEncoder("default")
    case object Html extends HighlightEncoder("html")
}

class HighlightDefinition(field: String) {

    val builder = new HighlightBuilder.Field(field)

    def fragmentSize(f: Int) = {
        builder.fragmentSize(f)
        this
    }

    def numberOfFragments(n: Int) = {
        builder.numOfFragments(n)
        this
    }

    def fragmentOffset(n: Int) = {
        builder.fragmentOffset(n)
        this
    }

    def highlighterType(`type`: String) = {
        builder.highlighterType(`type`)
        this
    }
}