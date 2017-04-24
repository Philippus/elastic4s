package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class HighlightFieldDefinition(field: String,
                                    boundaryChars: Option[Array[Char]] = None,
                                    boundaryMaxScan: Option[Int] = None,
                                    forceSource: Option[Boolean] = None,
                                    fragmenter: Option[String] = None,
                                    fragmentOffset: Option[Int] = None,
                                    fragmentSize: Option[Int] = None,
                                    highlighterType: Option[String] = None,
                                    highlightFilter: Option[Boolean] = None,
                                    highlightQuery: Option[QueryDefinition] = None,
                                    order: Option[String] = None,
                                    noMatchSize: Option[Int] = None,
                                    numOfFragments: Option[Int] = None,
                                    postTags: Seq[String] = Nil,
                                    preTags: Seq[String] = Nil,
                                    requireFieldMatch: Option[Boolean] = None,
                                    matchedFields: Seq[String] = Nil,
                                    phraseLimit: Option[Int] = None) {

  def boundaryChars(chars: Array[Char]): HighlightFieldDefinition = copy(boundaryChars = chars.some)
  def boundaryChars(chars: String): HighlightFieldDefinition = copy(boundaryChars = chars.toCharArray.some)
  def boundaryMaxScan(boundaryMaxScan: Int): HighlightFieldDefinition = copy(boundaryMaxScan = boundaryMaxScan.some)

  def fragmenter(fragmenter: String): HighlightFieldDefinition = copy(fragmenter = fragmenter.some)
  def fragmentOffset(fragmentOffset: Int): HighlightFieldDefinition = copy(fragmentOffset = fragmentOffset.some)
  def fragmentSize(fragmentSize: Int): HighlightFieldDefinition = copy(fragmentSize = fragmentSize.some)

  def requireFieldMatch(requireFieldMatch: Boolean): HighlightFieldDefinition = copy(requireFieldMatch = requireFieldMatch.some)

  def forceSource(forceSource: Boolean): HighlightFieldDefinition = copy(forceSource = forceSource.some)

  def highlightFilter(highlightFilter: Boolean): HighlightFieldDefinition = copy(highlightFilter = highlightFilter.some)
  def highlighterType(highlighterType: String): HighlightFieldDefinition = copy(highlighterType = highlighterType.some)

  def matchedFields(first: String, rest: String*): HighlightFieldDefinition = matchedFields(first +: rest)
  def matchedFields(fields: Iterable[String]): HighlightFieldDefinition = copy(matchedFields = fields.toSeq)

  def noMatchSize(noMatchSize: Int): HighlightFieldDefinition = copy(noMatchSize = noMatchSize.some)

  def numberOfFragments(numOfFragments: Int): HighlightFieldDefinition = copy(numOfFragments = numOfFragments.some)

  def order(order: String): HighlightFieldDefinition = copy(order = order.some)

  def query(query: QueryDefinition): HighlightFieldDefinition = copy(highlightQuery = query.some)

  def phraseLimit(limit: Int): HighlightFieldDefinition = copy(phraseLimit = limit.some)

  def preTag(tags: String*): HighlightFieldDefinition = preTag(tags)
  def preTag(tags: Iterable[String]): HighlightFieldDefinition = copy(preTags = tags.toSeq)

  def postTag(tags: String*): HighlightFieldDefinition = postTag(tags)
  def postTag(tags: Iterable[String]): HighlightFieldDefinition = copy(postTags = tags.toSeq)

  def requireFieldMatchScan(req: Boolean): HighlightFieldDefinition = copy(requireFieldMatch = req.some)
}
