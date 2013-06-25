package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait HighlightOrder
case object HighlightOrder {
    case object Score extends HighlightOrder
}

sealed trait TagSchema
case object TagSchema {
    case object Styled extends TagSchema
}

sealed trait HighlightEncoder
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

class PrepareHighlightFieldBuilder {
    def field(field: String) = new HighlightFieldBuilder(field)
}

class HighlightFieldBuilder(field: String) {

    val builder = new org.elasticsearch.search.highlight.HighlightBuilder.Field(field)

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

    def highlighterType(`type`: String) = {
        builder.highlighterType(`type`)
        this
    }

    def and(field: String) = new HighlightFieldBuilder(field: String)
}