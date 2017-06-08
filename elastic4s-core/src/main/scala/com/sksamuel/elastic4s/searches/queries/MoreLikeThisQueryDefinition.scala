package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DocumentRef

case class MoreLikeThisItem(index: String, `type`: String, id: String, routing: Option[String] = None)

case class ArtificialDocument(index: String, `type`: String, doc: String, routing: Option[String] = None)

case class MoreLikeThisQueryDefinition(fields: Seq[String],
                                       likeTexts: Seq[String] = Nil,
                                       likeDocs: Seq[MoreLikeThisItem] = Nil,
                                       analyzer: Option[String] = None,
                                       artificialDocs: Seq[ArtificialDocument] = Nil,
                                       boost: Option[Double] = None,
                                       boostTerms: Option[Double] = None,
                                       failOnUnsupportedField: Option[Boolean] = None,
                                       include: Option[Boolean] = None,
                                       minDocFreq: Option[Int] = None,
                                       maxDocFreq: Option[Int] = None,
                                       minWordLength: Option[Int] = None,
                                       maxWordLength: Option[Int] = None,
                                       minTermFreq: Option[Int] = None,
                                       maxQueryTerms: Option[Int] = None,
                                       minShouldMatch: Option[String] = None,
                                       unlikeTexts: Seq[String] = Nil,
                                       unlikeDocs: Seq[MoreLikeThisItem] = Nil,
                                       stopWords: Seq[String] = Nil,
                                       queryName: Option[String] = None) extends QueryDefinition {

  def analyzer(analyzer: String): MoreLikeThisQueryDefinition = copy(analyzer = Some(analyzer))

  def artificialDocs(first: ArtificialDocument, rest: ArtificialDocument*): MoreLikeThisQueryDefinition =
    artificialDocs(first +: rest)

  def artificialDocs(docs: Iterable[ArtificialDocument]): MoreLikeThisQueryDefinition =
    copy(artificialDocs = docs.toSeq)

  def unlikeText(first: String, rest: String*): MoreLikeThisQueryDefinition = unlikeText(first +: rest)
  def unlikeText(unlikes: Iterable[String]): MoreLikeThisQueryDefinition = copy(unlikeTexts = unlikeTexts ++ unlikes)

  def unlikeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQueryDefinition =
    unlikeItems(first +: rest)

  def unlikeItems(unlikes: Iterable[MoreLikeThisItem]): MoreLikeThisQueryDefinition =
    copy(unlikeDocs = unlikeDocs ++ unlikes)

  def unlikeDocs(unlikes: Iterable[DocumentRef]): MoreLikeThisQueryDefinition =
    unlikeItems(unlikes.map { d => MoreLikeThisItem(d.index, d.`type`, d.id) })

  def include(inc: Boolean): MoreLikeThisQueryDefinition = copy(include = Some(inc))

  def failOnUnsupportedField(failOnUnsupportedField: Boolean): MoreLikeThisQueryDefinition =
    copy(failOnUnsupportedField = Some(failOnUnsupportedField))

  def minTermFreq(minTermFreq: Int): MoreLikeThisQueryDefinition = copy(minTermFreq = Some(minTermFreq))

  def stopWords(stopWords: Iterable[String]): MoreLikeThisQueryDefinition = copy(stopWords = stopWords.toSeq)
  def stopWords(first: String, rest: String*): MoreLikeThisQueryDefinition = stopWords(first +: rest)

  def minWordLength(minWordLen: Int): MoreLikeThisQueryDefinition = copy(minWordLength = Some(minWordLen))
  def maxWordLength(maxWordLen: Int): MoreLikeThisQueryDefinition = copy(maxWordLength = Some(maxWordLen))

  def boost(boost: Double): MoreLikeThisQueryDefinition = copy(boost = Some(boost))
  def boostTerms(boostTerms: Double): MoreLikeThisQueryDefinition = copy(boostTerms = Some(boostTerms))

  def maxQueryTerms(maxQueryTerms: Int): MoreLikeThisQueryDefinition = copy(maxQueryTerms = Some(maxQueryTerms))
  def minShouldMatch(minShouldMatch: String): MoreLikeThisQueryDefinition = copy(minShouldMatch = Some(minShouldMatch))

  def minDocFreq(minDocFreq: Int): MoreLikeThisQueryDefinition = copy(minDocFreq = Some(minDocFreq))
  def maxDocFreq(maxDocFreq: Int): MoreLikeThisQueryDefinition = copy(maxDocFreq = Some(maxDocFreq))

  def queryName(queryName: String): MoreLikeThisQueryDefinition = copy(queryName = Some(queryName))
}
