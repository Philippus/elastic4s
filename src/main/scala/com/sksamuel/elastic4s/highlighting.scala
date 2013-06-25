package com.sksamuel.elastic4s

import scala.collection.mutable.ListBuffer
import org.elasticsearch.search.highlight.HighlightBuilder

/** @author Stephen Samuel */
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

case class Highlight(fields: Iterable[HighlightField] = Nil,
                     preTags: Seq[String] = Nil,
                     postTags: Seq[String] = Nil,
                     encoder: Option[HighlightEncoder] = None,
                     order: Option[HighlightOrder] = None,
                     tagSchema: Option[TagSchema] = None,
                     requireFieldMatch: Boolean = false,
                     boundary_chars: Option[String] = None,
                     boundary_max_scan: Int = 20) {
    def builder: org.elasticsearch.search.highlight.HighlightBuilder = {
        val builder = new org.elasticsearch.search.highlight.HighlightBuilder()
          .postTags(postTags: _*)
          .preTags(preTags: _*)
          .requireFieldMatch(requireFieldMatch)
        encoder.foreach(arg => builder.encoder(arg.toString.toLowerCase))
        order.foreach(arg => builder.order(arg.toString.toLowerCase))
        tagSchema.foreach(arg => builder.tagsSchema(arg.toString.toLowerCase))
        builder
    }
}
case class HighlightField(name: String, fragmentSize: Int = 100, numberOfFragments: Int = 5)

class HighlightBuilder {

    val buffer = new ListBuffer[HighlightBuilder.Field]

    def field(fieldName: String) = {
        val builder = new org.elasticsearch.search.highlight.HighlightBuilder.Field(fieldName)
        buffer.append(builder)
        this
    }

    def size(s: Int) = fragmentSize(s)
    def fragmentSize(f: Int) = {
        buffer.last.fragmentSize(f)
        this
    }

    def number(n: Int) = numberOfFragments(n)
    def numberOfFragments(n: Int) = {
        buffer.last.numOfFragments(n)
        this
    }

    def offset(n: Int) = fragmentOffset(n)
    def fragmentOffset(n: Int) = {
        buffer.last.fragmentOffset(n)
        this
    }

    def highlighterType(`type`: String) = {
        buffer.last.highlighterType(`type`)
        this
    }

    def and(fieldName: String) = field(fieldName)
}