package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.{MoreLikeThisQueryBuilder, QueryBuilders}

case class MoreLikeThisQueryDefinition(fields: Seq[String]) extends QueryDefinition {

  val _builder = QueryBuilders.moreLikeThisQuery(fields.toArray)
  val builder = _builder

  def like(first: String, rest: String*): this.type = like(first +: rest)
  def like(likes: Iterable[String]): this.type = {
    _builder.like(likes.toSeq: _*)
    this
  }

  def like(item: Item, rest: Item*): this.type = like(item +: rest)
  def like(items: Seq[Item]): this.type = {
    builder.like(items.map(item => new MoreLikeThisQueryBuilder.Item(item.index, item.`type`, item.id)): _ *)
    this
  }

  def analyzer(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  @deprecated("Use unlike", "2.1.0")
  def ignoreLike(first: String, rest: String*): this.type = unlike(first +: rest)
  @deprecated("Use unlike", "2.1.0")
  def ignoreLike(likes: Iterable[String]): this.type = {
    _builder.unlike(likes.toSeq: _*)
    this
  }

  def unlike(first: String, rest: String*): this.type = unlike(first +: rest)
  def unlike(likes: Iterable[String]): this.type = {
    _builder.unlike(likes.toSeq: _*)
    this
  }

  def analyser(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  @deprecated("deprecated in elasticsearch", "2.0.0")
  def ids(ids: String*): this.type = {
    _builder.ids(ids: _*)
    this
  }

  def exclude(): this.type = {
    _builder.include(false)
    this
  }

  def include(): this.type = {
    _builder.include(true)
    this
  }

  def failOnUnsupportedField(): this.type = {
    _builder.failOnUnsupportedField(true)
    this
  }

  def notFailOnUnsupportedField(): this.type = {
    _builder.failOnUnsupportedField(false)
    this
  }

  def minTermFreq(freq: Int): this.type = {
    _builder.minTermFreq(freq)
    this
  }

  def stopWords(stopWords: String*): this.type = {
    _builder.stopWords(stopWords: _*)
    this
  }

  def maxWordLength(maxWordLen: Int): this.type = {
    _builder.maxWordLength(maxWordLen)
    this
  }

  def minWordLength(minWordLen: Int): this.type = {
    _builder.minWordLength(minWordLen)
    this
  }

  def boostTerms(boostTerms: Double): this.type = {
    _builder.boostTerms(boostTerms.toFloat)
    this
  }

  def boost(boost: Double): this.type = {
    _builder.boost(boost.toFloat)
    this
  }

  def maxQueryTerms(maxQueryTerms: Int): this.type = {
    _builder.maxQueryTerms(maxQueryTerms)
    this
  }

  def minDocFreq(minDocFreq: Int): this.type = {
    _builder.minDocFreq(minDocFreq)
    this
  }

  def maxDocFreq(maxDocFreq: Int): this.type = {
    _builder.maxDocFreq(maxDocFreq)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
