package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.common.DocumentRef

case class MoreLikeThisItem(ref: DocumentRef, routing: Option[String] = None)
object MoreLikeThisItem {
  def apply(index: String, `type`: String, id: String): MoreLikeThisItem =
    MoreLikeThisItem(DocumentRef(index, `type`, id))
  def apply(index: String, `type`: String, id: String, routing: String): MoreLikeThisItem =
    MoreLikeThisItem(DocumentRef(index, `type`, id), Some(routing))
}

case class ArtificialDocument(index: String, `type`: String, doc: String, routing: Option[String] = None)

case class MoreLikeThisQuery(fields: Seq[String],
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
                             queryName: Option[String] = None) extends Query {

  def analyzer(analyzer: String): MoreLikeThisQuery = copy(analyzer = Some(analyzer))

  def artificialDocs(first: ArtificialDocument, rest: ArtificialDocument*): MoreLikeThisQuery =
    artificialDocs(first +: rest)

  def artificialDocs(docs: Iterable[ArtificialDocument]): MoreLikeThisQuery =
    copy(artificialDocs = docs.toSeq)

  def unlikeText(first: String, rest: String*): MoreLikeThisQuery = unlikeText(first +: rest)
  def unlikeText(unlikes: Iterable[String]): MoreLikeThisQuery    = copy(unlikeTexts = unlikeTexts ++ unlikes)

  def unlikeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQuery =
    unlikeItems(first +: rest)

  def unlikeItems(unlikes: Iterable[MoreLikeThisItem]): MoreLikeThisQuery =
    copy(unlikeDocs = unlikeDocs ++ unlikes)

  def unlikeDocs(first: DocumentRef, rest: DocumentRef*): MoreLikeThisQuery =
    unlikeDocs(first +: rest)

  def unlikeDocs(unlikes: Iterable[DocumentRef]): MoreLikeThisQuery =
    unlikeItems(unlikes.map { d =>
      MoreLikeThisItem(d)
    })

  def include(inc: Boolean): MoreLikeThisQuery = copy(include = Some(inc))

  def failOnUnsupportedField(failOnUnsupportedField: Boolean): MoreLikeThisQuery =
    copy(failOnUnsupportedField = Some(failOnUnsupportedField))

  def minTermFreq(minTermFreq: Int): MoreLikeThisQuery = copy(minTermFreq = Some(minTermFreq))

  def stopWords(stopWords: Iterable[String]): MoreLikeThisQuery  = copy(stopWords = stopWords.toSeq)
  def stopWords(first: String, rest: String*): MoreLikeThisQuery = stopWords(first +: rest)

  def minWordLength(minWordLen: Int): MoreLikeThisQuery = copy(minWordLength = Some(minWordLen))
  def maxWordLength(maxWordLen: Int): MoreLikeThisQuery = copy(maxWordLength = Some(maxWordLen))

  def boost(boost: Double): MoreLikeThisQuery           = copy(boost = Some(boost))
  def boostTerms(boostTerms: Double): MoreLikeThisQuery = copy(boostTerms = Some(boostTerms))

  def maxQueryTerms(maxQueryTerms: Int): MoreLikeThisQuery      = copy(maxQueryTerms = Some(maxQueryTerms))
  def minShouldMatch(minShouldMatch: String): MoreLikeThisQuery = copy(minShouldMatch = Some(minShouldMatch))

  def minDocFreq(minDocFreq: Int): MoreLikeThisQuery = copy(minDocFreq = Some(minDocFreq))
  def maxDocFreq(maxDocFreq: Int): MoreLikeThisQuery = copy(maxDocFreq = Some(maxDocFreq))

  def queryName(queryName: String): MoreLikeThisQuery = copy(queryName = Some(queryName))
}
