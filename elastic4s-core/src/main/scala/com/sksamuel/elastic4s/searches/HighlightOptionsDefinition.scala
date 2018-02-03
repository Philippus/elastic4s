package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class HighlightOptionsDefinition(encoder: Option[String] = None,
                                      tagsSchema: Option[String] = None,
                                      useExplicitFieldOrder: Option[Boolean] = None,
                                      boundaryChars: Option[String] = None,
                                      boundaryMaxScan: Option[Int] = None,
                                      boundaryScanner: Option[String] = None,
                                      boundaryScannerLocale: Option[String] = None,
                                      fragmenter: Option[String] = None,
                                      fragmentSize: Option[Int] = None,
                                      forceSource: Option[Boolean] = None,
                                      highlighterType: Option[String] = None,
                                      highlightFilter: Option[Boolean] = None,
                                      highlightQuery: Option[Query] = None,
                                      noMatchSize: Option[Int] = None,
                                      numOfFragments: Option[Int] = None,
                                      order: Option[String] = None,
                                      phraseLimit: Option[Int] = None,
                                      postTags: Seq[String] = Nil,
                                      preTags: Seq[String] = Nil,
                                      requireFieldMatch: Option[Boolean] = None,
                                      options: Option[Map[String, AnyRef]] = None) {

  def boundaryChars(boundaryChars: String): HighlightOptionsDefinition  = copy(boundaryChars = boundaryChars.some)
  def boundaryMaxScan(boundaryMaxScan: Int): HighlightOptionsDefinition = copy(boundaryMaxScan = boundaryMaxScan.some)
  def boundaryScanner(boundaryScanner: String): HighlightOptionsDefinition =
    copy(boundaryScanner = boundaryScanner.some)
  def boundaryScannerLocale(locale: String): HighlightOptionsDefinition = copy(boundaryScannerLocale = locale.some)

  def encoder(encoder: String): HighlightOptionsDefinition       = copy(encoder = encoder.some)
  def tagsSchema(tagsSchema: String): HighlightOptionsDefinition = copy(tagsSchema = tagsSchema.some)

  def useExplicitFieldOrder(useExplicitFieldOrder: Boolean): HighlightOptionsDefinition =
    copy(useExplicitFieldOrder = useExplicitFieldOrder.some)

  def fragmenter(fragmenter: String): HighlightOptionsDefinition    = copy(fragmenter = fragmenter.some)
  def fragmentSize(fragmentSize: Int): HighlightOptionsDefinition   = copy(fragmentSize = fragmentSize.some)
  def forceSource(forceSource: Boolean): HighlightOptionsDefinition = copy(forceSource = forceSource.some)

  def highlighterType(highlighterType: String): HighlightOptionsDefinition =
    copy(highlighterType = highlighterType.some)

  def highlightFilter(highlightFilter: Boolean): HighlightOptionsDefinition =
    copy(highlightFilter = highlightFilter.some)

  def highlightQuery(highlightQuery: Query): HighlightOptionsDefinition =
    copy(highlightQuery = highlightQuery.some)

  def noMatchSize(noMatchSize: Int): HighlightOptionsDefinition       = copy(noMatchSize = noMatchSize.some)
  def numOfFragments(numOfFragments: Int): HighlightOptionsDefinition = copy(numOfFragments = numOfFragments.some)
  def order(order: String): HighlightOptionsDefinition                = copy(order = order.some)
  def phraseLimit(phraseLimit: Int): HighlightOptionsDefinition       = copy(phraseLimit = phraseLimit.some)

  def postTags(first: String, rest: String*): HighlightOptionsDefinition = postTags(first +: rest)
  def preTags(first: String, rest: String*): HighlightOptionsDefinition  = preTags(first +: rest)

  def postTags(postTags: Iterable[String]): HighlightOptionsDefinition = copy(postTags = postTags.toSeq)
  def preTags(preTags: Iterable[String]): HighlightOptionsDefinition   = copy(preTags = preTags.toSeq)

  def options(newOptions: Map[String, AnyRef]): HighlightOptionsDefinition = copy(options = newOptions.some)

  def requireFieldMatch(requireFieldMatch: Boolean): HighlightOptionsDefinition =
    copy(requireFieldMatch = requireFieldMatch.some)

}
