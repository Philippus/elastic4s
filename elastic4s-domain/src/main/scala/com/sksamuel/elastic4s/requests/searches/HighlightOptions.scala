package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class HighlightOptions(encoder: Option[String] = None,
                            tagsSchema: Option[String] = None,
                            useExplicitFieldOrder: Option[Boolean] = None,
                            boundaryChars: Option[String] = None,
                            boundaryMaxScan: Option[Int] = None,
                            boundaryScanner: Option[String] = None,
                            boundaryScannerLocale: Option[String] = None,
                            fragmenter: Option[String] = None,
                            fragmentSize: Option[Int] = None,
                            @deprecated("This parameter has no effect", "8.15.0")
                            forceSource: Option[Boolean] = None,
                            highlighterType: Option[String] = None,
                            highlightFilter: Option[Boolean] = None,
                            highlightQuery: Option[Query] = None,
                            noMatchSize: Option[Int] = None,
                            numOfFragments: Option[Int] = None,
                            order: Option[String] = None,
                            phraseLimit: Option[Int] = None,
                            maxAnalyzedOffset: Option[Int] = None,
                            postTags: Seq[String] = Nil,
                            preTags: Seq[String] = Nil,
                            requireFieldMatch: Option[Boolean] = None,
                            options: Option[Map[String, Any]] = None,
                            fragmentOffset: Option[Int] = None,
                            matchedFields: Seq[String] = Nil) {

  def boundaryChars(boundaryChars: String): HighlightOptions = copy(boundaryChars = boundaryChars.some)
  def boundaryMaxScan(boundaryMaxScan: Int): HighlightOptions = copy(boundaryMaxScan = boundaryMaxScan.some)
  def boundaryScanner(boundaryScanner: String): HighlightOptions =
    copy(boundaryScanner = boundaryScanner.some)
  def boundaryScannerLocale(locale: String): HighlightOptions = copy(boundaryScannerLocale = locale.some)

  def encoder(encoder: String): HighlightOptions = copy(encoder = encoder.some)
  def tagsSchema(tagsSchema: String): HighlightOptions = copy(tagsSchema = tagsSchema.some)

  def useExplicitFieldOrder(useExplicitFieldOrder: Boolean): HighlightOptions =
    copy(useExplicitFieldOrder = useExplicitFieldOrder.some)

  def fragmenter(fragmenter: String): HighlightOptions = copy(fragmenter = fragmenter.some)
  def fragmentOffset(fragmentOffset: Int): HighlightOptions = copy(fragmentOffset = fragmentOffset.some)
  def fragmentSize(fragmentSize: Int): HighlightOptions = copy(fragmentSize = fragmentSize.some)
  @deprecated("This method has no effect", "8.15.0")
  def forceSource(forceSource: Boolean): HighlightOptions = copy(forceSource = forceSource.some)

  def highlighterType(highlighterType: String): HighlightOptions =
    copy(highlighterType = highlighterType.some)

  def highlightFilter(highlightFilter: Boolean): HighlightOptions =
    copy(highlightFilter = highlightFilter.some)

  def highlightQuery(highlightQuery: Query): HighlightOptions =
    copy(highlightQuery = highlightQuery.some)

  def matchedFields(first: String, rest: String*): HighlightOptions = matchedFields(first +: rest)
  def matchedFields(fields: Iterable[String]): HighlightOptions = copy(matchedFields = fields.toSeq)

  def noMatchSize(noMatchSize: Int): HighlightOptions = copy(noMatchSize = noMatchSize.some)
  def numOfFragments(numOfFragments: Int): HighlightOptions = copy(numOfFragments = numOfFragments.some)
  def order(order: String): HighlightOptions = copy(order = order.some)
  def phraseLimit(phraseLimit: Int): HighlightOptions = copy(phraseLimit = phraseLimit.some)
  def maxAnalyzedOffset(maxAnalyzedOffset: Int): HighlightOptions = copy(maxAnalyzedOffset = maxAnalyzedOffset.some)

  def postTags(first: String, rest: String*): HighlightOptions = postTags(first +: rest)
  def preTags(first: String, rest: String*): HighlightOptions = preTags(first +: rest)

  def postTags(postTags: Iterable[String]): HighlightOptions = copy(postTags = postTags.toSeq)
  def preTags(preTags: Iterable[String]): HighlightOptions = copy(preTags = preTags.toSeq)

  def options(newOptions: Map[String, Any]): HighlightOptions = copy(options = newOptions.some)

  def requireFieldMatch(requireFieldMatch: Boolean): HighlightOptions =
    copy(requireFieldMatch = requireFieldMatch.some)

}
