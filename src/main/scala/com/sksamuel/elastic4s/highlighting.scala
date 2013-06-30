package com.sksamuel.elastic4s

import org.elasticsearch.search.highlight.HighlightBuilder

/** @author Stephen Samuel */
trait HighlightDsl {
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

/*
preTags: Seq[String] = Nil,
postTags: Seq[String] = Nil,
encoder: Option[HighlightEncoder] = None,
order: Option[HighlightOrder] = None,
tagSchema: Option[TagSchema] = None,
requireFieldMatch: Boolean = false,
boundary_chars: Option[String] = None,
boundary_max_scan: Int = 20
*/
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