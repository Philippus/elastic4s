package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.search.Scroll

/** @author Stephen Samuel */
trait MoreLikeThisDsl {

  def morelike = mlt
  case object mlt {
    def id(id: Any) = new MltExpectsIndex(id.toString)
  }

  class MltExpectsIndex(id: String) {
    def in(in: String) = in.split("/").toList match {
      case idx :: Nil => new MoreLikeThisDefinition(idx, null, id)
      case idx :: t :: Nil => new MoreLikeThisDefinition(idx, t, id)
      case _ => throw new RuntimeException
    }
  }
}

class MoreLikeThisDefinition(index: String, `type`: String, id: String) {

  private val _builder = Requests.moreLikeThisRequest(index).`type`(`type`).id(id)
  def build = _builder

  def boostTerms(boostTerms: Double): this.type = {
    _builder.boostTerms(boostTerms.toFloat)
    this
  }
  def fields(fields: String*): this.type = {
    _builder.fields(fields: _*)
    this
  }
  def from(i: Int): this.type = {
    _builder.searchFrom(i)
    this
  }
  def include(include: Boolean): this.type = {
    _builder.include(include)
    this
  }
  def limit(sizeHint: Int): this.type = size(sizeHint)
  def minTermFreq(freq: Int): this.type = {
    _builder.minTermFreq(freq)
    this
  }
  def maxQueryTerms(maxQueryTerms: Int): this.type = {
    _builder.maxQueryTerms(maxQueryTerms)
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
  def maxDocFreq(maxDocFreq: Int): this.type = {
    _builder.maxDocFreq(maxDocFreq)
    this
  }
  def minDocFreq(minDocFreq: Int): this.type = {
    _builder.minDocFreq(minDocFreq)
    this
  }
  def percentTermsToMatch(percentTermsToMatch: Double): this.type = {
    _builder.percentTermsToMatch(percentTermsToMatch.toFloat)
    this
  }
  def routing(routing: String): this.type = {
    _builder.routing(routing)
    this
  }
  def size(sizeHint: Int): this.type = {
    _builder.searchSize(sizeHint)
    this
  }
  def start(i: Int): this.type = from(i)
  def stopWords(stopWords: String*): this.type = {
    _builder.stopWords(stopWords: _*)
    this
  }
  def searchSize(searchSize: Int): this.type = {
    _builder.searchSize(searchSize)
    this
  }
  def searchScroll(searchScroll: Scroll): this.type = {
    _builder.searchScroll(searchScroll)
    this
  }
}
