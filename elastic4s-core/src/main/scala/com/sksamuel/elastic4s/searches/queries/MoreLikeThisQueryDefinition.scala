package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.{MoreLikeThisQueryBuilder, QueryBuilders}

@deprecated("use DocumentRef", "3.0.0")
case class MoreLikeThisItem(index: String, `type`: String, id: String) {
  def build: MoreLikeThisQueryBuilder.Item = new MoreLikeThisQueryBuilder.Item(index, `type`, id)
}

case class MoreLikeThisQueryDefinition(fields: Seq[String],
                                       likeTexts: Seq[String] = Nil,
                                       likeDocs: Seq[DocumentRef] = Nil,
                                       analyzer: Option[String] = None,
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
                                       unlikeDocs: Seq[DocumentRef] = Nil,
                                       stopWords: Seq[String] = Nil,
                                       queryName: Option[String] = None) extends QueryDefinition {

  def builder: MoreLikeThisQueryBuilder = {

    val builder = QueryBuilders.moreLikeThisQuery(
      fields.toArray,
      likeTexts.toArray,
      likeDocs.map { doc => new MoreLikeThisQueryBuilder.Item(doc.index, doc.`type`, doc.id) }.toArray
    )

    analyzer.foreach(builder.analyzer)
    boost.map(_.toFloat).foreach(builder.boost)
    boostTerms.map(_.toFloat).foreach(builder.boostTerms)
    maxWordLength.foreach(builder.maxWordLength)
    failOnUnsupportedField.foreach(builder.failOnUnsupportedField)
    include.foreach(builder.include)
    maxDocFreq.foreach(builder.maxDocFreq)
    maxQueryTerms.foreach(builder.maxQueryTerms)
    minDocFreq.foreach(builder.minDocFreq)
    minShouldMatch.foreach(builder.minimumShouldMatch)
    minTermFreq.foreach(builder.minTermFreq)
    minWordLength.foreach(builder.minWordLength)
    queryName.foreach(builder.queryName)

    builder.unlike(unlikeDocs.toArray.map { doc => new MoreLikeThisQueryBuilder.Item(doc.index, doc.`type`, doc.id) })
    builder.unlike(unlikeTexts.toArray)
    builder.stopWords(stopWords: _*)
  }

  def analyzer(analyzer: String): MoreLikeThisQueryDefinition = copy(analyzer = Some(analyzer))

  def unlikeText(first: String, rest: String*): MoreLikeThisQueryDefinition = unlikeText(first +: rest)
  def unlikeText(unlikes: Iterable[String]): MoreLikeThisQueryDefinition = copy(unlikeTexts = unlikeTexts ++ unlikes)

  def unlikeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQueryDefinition =
    unlikeItems(first +: rest)

  def unlikeItems(unlikes: Iterable[MoreLikeThisItem]): MoreLikeThisQueryDefinition =
    unlikeDocs(unlikes.map { item => DocumentRef(item.index, item.`type`, item.id) })

  def unlikeDocs(unlikes: Iterable[DocumentRef]): MoreLikeThisQueryDefinition =
    copy(unlikeDocs = unlikeDocs ++ unlikes)

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
