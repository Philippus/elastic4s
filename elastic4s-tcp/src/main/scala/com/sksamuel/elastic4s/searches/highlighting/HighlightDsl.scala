package com.sksamuel.elastic4s.searches.highlighting

import com.sksamuel.elastic4s.searches.{QueryBuilderFn, QueryDefinition}
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder

import scala.language.implicitConversions

trait HighlightDsl {

  @deprecated("use highlight(name)", "5.0.0")
  implicit def string2highlightfield(name: String): HighlightFieldDefinition = HighlightFieldDefinition(name)

  @deprecated("use highlightOptions()", "5.0.0")
  def options = HighlightOptionsDefinition()

  def highlightOptions() = HighlightOptionsDefinition()

  def highlight(field: String) = HighlightFieldDefinition(field)
}

import com.sksamuel.exts.OptionImplicits._

case class HighlightOptionsDefinition(encoder: Option[String] = None,
                                      tagsSchema: Option[String] = None,
                                      useExplicitFieldOrder: Option[Boolean] = None,
                                      boundaryChars: Option[String] = None,
                                      boundaryMaxScan: Option[Int] = None,
                                      fragmenter: Option[String] = None,
                                      fragmentSize: Option[Int] = None,
                                      forceSource: Option[Boolean] = None,
                                      highlighterType: Option[String] = None,
                                      highlightFilter: Option[Boolean] = None,
                                      highlightQuery: Option[QueryDefinition] = None,
                                      noMatchSize: Option[Int] = None,
                                      numOfFragments: Option[Int] = None,
                                      order: Option[String] = None,
                                      phraseLimit: Option[Int] = None,
                                      postTags: Seq[String] = Nil,
                                      preTags: Seq[String] = Nil,
                                      requireFieldMatch: Option[Boolean] = None
                                     ) {
  def populate(builder: HighlightBuilder) = {
    encoder.foreach(builder.encoder)
    tagsSchema.foreach(builder.tagsSchema)
    forceSource.foreach(bool => builder.forceSource(bool))
    useExplicitFieldOrder.foreach(builder.useExplicitFieldOrder)
    boundaryChars.map(_.toCharArray).foreach(builder.boundaryChars)
    boundaryMaxScan.foreach(int => builder.boundaryMaxScan(int))
    fragmenter.foreach(builder.fragmenter)
    fragmentSize.foreach(int => builder.fragmentSize(int))
    highlighterType.foreach(builder.highlighterType)
    highlightFilter.foreach(bool => builder.highlightFilter(bool))
    highlightQuery.map(QueryBuilderFn.apply).foreach(builder.highlightQuery)
    noMatchSize.foreach(int => builder.noMatchSize(int))
    numOfFragments.foreach(int => builder.numOfFragments(int))
    order.foreach(builder.order)
    phraseLimit.foreach(int => builder.phraseLimit(int))
    if (postTags.nonEmpty)
      builder.postTags(postTags: _*)
    if (preTags.nonEmpty)
      builder.preTags(preTags: _*)
    requireFieldMatch.foreach(bool => builder.requireFieldMatch(bool))
  }

  def boundaryChars(boundaryChars: String): HighlightOptionsDefinition = copy(boundaryChars = boundaryChars.some)
  def boundaryMaxScan(boundaryMaxScan: Int): HighlightOptionsDefinition = copy(boundaryMaxScan = boundaryMaxScan.some)

  def encoder(encoder: String): HighlightOptionsDefinition = copy(encoder = encoder.some)
  def tagsSchema(tagsSchema: String): HighlightOptionsDefinition = copy(tagsSchema = tagsSchema.some)

  def useExplicitFieldOrder(useExplicitFieldOrder: Boolean): HighlightOptionsDefinition =
    copy(useExplicitFieldOrder = useExplicitFieldOrder.some)

  def fragmenter(fragmenter: String): HighlightOptionsDefinition = copy(fragmenter = fragmenter.some)
  def fragmentSize(fragmentSize: Int): HighlightOptionsDefinition = copy(fragmentSize = fragmentSize.some)
  def forceSource(forceSource: Boolean): HighlightOptionsDefinition = copy(forceSource = forceSource.some)

  def highlighterType(highlighterType: String): HighlightOptionsDefinition =
    copy(highlighterType = highlighterType.some)

  def highlightFilter(highlightFilter: Boolean): HighlightOptionsDefinition =
    copy(highlightFilter = highlightFilter.some)

  def highlightQuery(highlightQuery: QueryDefinition): HighlightOptionsDefinition =
    copy(highlightQuery = highlightQuery.some)

  def noMatchSize(noMatchSize: Int): HighlightOptionsDefinition = copy(noMatchSize = noMatchSize.some)
  def numOfFragments(numOfFragments: Int): HighlightOptionsDefinition = copy(numOfFragments = numOfFragments.some)
  def order(order: String): HighlightOptionsDefinition = copy(order = order.some)
  def phraseLimit(phraseLimit: Int): HighlightOptionsDefinition = copy(phraseLimit = phraseLimit.some)

  def postTags(first: String, rest: String*): HighlightOptionsDefinition = postTags(first +: rest)
  def preTags(first: String, rest: String*): HighlightOptionsDefinition = preTags(first +: rest)

  def postTags(postTags: Iterable[String]): HighlightOptionsDefinition = copy(postTags = postTags.toSeq)
  def preTags(preTags: Iterable[String]): HighlightOptionsDefinition = copy(preTags = preTags.toSeq)

  def requireFieldMatch(requireFieldMatch: Boolean): HighlightOptionsDefinition =
    copy(requireFieldMatch = requireFieldMatch.some)

}
