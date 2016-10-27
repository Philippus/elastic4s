package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.{MoreLikeThisQueryBuilder, QueryBuilders}

case class MoreLikeThisItem(index: String, `type`: String, id: String) {
  def build: MoreLikeThisQueryBuilder.Item = new MoreLikeThisQueryBuilder.Item(index, `type`, id)
}

case class MoreLikeThisQueryDefinition(fields: Seq[String],
                                       likeTexts: Seq[String],
                                       likeItems: Seq[MoreLikeThisItem],
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
                                       unlikeItems: Seq[MoreLikeThisItem] = Nil,
                                       stopWords: Seq[String] = Nil,
                                       queryName: Option[String] = None) extends QueryDefinition {

  def builder: MoreLikeThisQueryBuilder = {
    val builder = QueryBuilders.moreLikeThisQuery(fields.toArray, likeTexts.toArray, likeItems.map(_.build).toArray)
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
    builder.unlike(unlikeItems.toArray.map(_.build))
    builder.unlike(unlikeTexts.toArray)
    builder.stopWords(stopWords: _*)
  }

  def analyzer(analyzer: String) = copy(analyzer = Some(analyzer))

  def unlikeText(first: String, rest: String*): MoreLikeThisQueryDefinition = unlikeText(first +: rest)
  def unlikeText(unlikes: Iterable[String]): MoreLikeThisQueryDefinition = copy(unlikeTexts = unlikeTexts ++ unlikes)

  def unlikeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQueryDefinition = unlikeItems(first +: rest)
  def unlikeItems(unlikes: Iterable[MoreLikeThisItem]): MoreLikeThisQueryDefinition = copy(unlikeItems = unlikeItems ++ unlikes.toSeq)

  def include(inc: Boolean) = copy(include = Some(inc))

  def failOnUnsupportedField(failOnUnsupportedField: Boolean) = copy(failOnUnsupportedField = Some(failOnUnsupportedField))

  def minTermFreq(minTermFreq: Int) = copy(minTermFreq = Some(minTermFreq))

  def stopWords(stopWords: Iterable[String]) = copy(stopWords = stopWords.toSeq)
  def stopWords(first: String, rest: String*) = stopWords(first +: rest)

  def minWordLength(minWordLen: Int) = copy(minWordLength = Some(minWordLen))
  def maxWordLength(maxWordLen: Int) = copy(maxWordLength = Some(maxWordLen))

  def boost(boost: Double) = copy(boost = Some(boost))
  def boostTerms(boostTerms: Double) = copy(boostTerms = Some(boostTerms))

  def maxQueryTerms(maxQueryTerms: Int) = copy(maxQueryTerms = Some(maxQueryTerms))
  def minShouldMatch(minShouldMatch: String) = copy(minShouldMatch = Some(minShouldMatch))

  def minDocFreq(minDocFreq: Int) = copy(minDocFreq = Some(minDocFreq))
  def maxDocFreq(maxDocFreq: Int) = copy(maxDocFreq = Some(maxDocFreq))

  def queryName(queryName: String) = copy(queryName = Some(queryName))
}
