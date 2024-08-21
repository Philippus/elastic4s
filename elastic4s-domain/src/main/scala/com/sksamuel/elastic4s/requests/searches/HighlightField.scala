package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class HighlightField(field: String,
                          boundaryChars: Option[Array[Char]] = None,
                          boundaryMaxScan: Option[Int] = None,
                          @deprecated("This parameter has no effect", "8.15.0")
                          forceSource: Option[Boolean] = None,
                          fragmenter: Option[String] = None,
                          fragmentOffset: Option[Int] = None,
                          fragmentSize: Option[Int] = None,
                          highlighterType: Option[String] = None,
                          highlightFilter: Option[Boolean] = None,
                          highlightQuery: Option[Query] = None,
                          order: Option[String] = None,
                          noMatchSize: Option[Int] = None,
                          numOfFragments: Option[Int] = None,
                          postTags: Seq[String] = Nil,
                          preTags: Seq[String] = Nil,
                          requireFieldMatch: Option[Boolean] = None,
                          matchedFields: Seq[String] = Nil,
                          phraseLimit: Option[Int] = None,
                          boundaryScanner: Option[String] = None,
                          boundaryScannerLocale: Option[String] = None,
                          options: Option[Map[String, Any]] = None,
                          encoder: Option[String] = None,
                          maxAnalyzedOffset: Option[Int] = None,
                          tagsSchema: Option[String] = None) {

  def boundaryChars(chars: Array[Char]): HighlightField = copy(boundaryChars = chars.some)
  def boundaryChars(chars: String): HighlightField = copy(boundaryChars = chars.toCharArray.some)
  def boundaryMaxScan(boundaryMaxScan: Int): HighlightField = copy(boundaryMaxScan = boundaryMaxScan.some)

  def boundaryScanner(scanner: String): HighlightField = copy(boundaryScanner = scanner.some)
  def boundaryScannerLocale(locale: String): HighlightField = copy(boundaryScannerLocale = locale.some)

  def encoder(encoder: String): HighlightField = copy(encoder = encoder.some)
  def fragmenter(fragmenter: String): HighlightField = copy(fragmenter = fragmenter.some)
  def fragmentOffset(fragmentOffset: Int): HighlightField = copy(fragmentOffset = fragmentOffset.some)
  def fragmentSize(fragmentSize: Int): HighlightField = copy(fragmentSize = fragmentSize.some)

  def requireFieldMatch(requireFieldMatch: Boolean): HighlightField =
    copy(requireFieldMatch = requireFieldMatch.some)

  @deprecated("This method has no effect", "8.15.0")
  def forceSource(forceSource: Boolean): HighlightField = copy(forceSource = forceSource.some)

  def highlightFilter(highlightFilter: Boolean): HighlightField = copy(highlightFilter = highlightFilter.some)
  def highlighterType(highlighterType: String): HighlightField = copy(highlighterType = highlighterType.some)

  def matchedFields(first: String, rest: String*): HighlightField = matchedFields(first +: rest)
  def matchedFields(fields: Iterable[String]): HighlightField = copy(matchedFields = fields.toSeq)

  def maxAnalyzedOffset(maxAnalyzedOffset: Int): HighlightField = copy(maxAnalyzedOffset = maxAnalyzedOffset.some)

  def noMatchSize(noMatchSize: Int): HighlightField = copy(noMatchSize = noMatchSize.some)

  def numberOfFragments(numOfFragments: Int): HighlightField = copy(numOfFragments = numOfFragments.some)

  def order(order: String): HighlightField = copy(order = order.some)

  def query(query: Query): HighlightField = copy(highlightQuery = query.some)

  def phraseLimit(limit: Int): HighlightField = copy(phraseLimit = limit.some)

  def preTag(tags: String*): HighlightField = preTag(tags)
  def preTag(tags: Iterable[String]): HighlightField = copy(preTags = tags.toSeq)

  def postTag(tags: String*): HighlightField = postTag(tags)
  def postTag(tags: Iterable[String]): HighlightField = copy(postTags = tags.toSeq)

  @deprecated("Use requireFieldMatch", "8.15.0")
  def requireFieldMatchScan(req: Boolean): HighlightField = requireFieldMatch(req)

  def tagsSchema(tagsSchema: String): HighlightField = copy(tagsSchema = tagsSchema.some)

  def options(newOptions: Map[String, Any]): HighlightField = copy(options = newOptions.some)

}
