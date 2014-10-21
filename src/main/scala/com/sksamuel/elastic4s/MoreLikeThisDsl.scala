package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

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

  def fields(fields: String*) = {
    _builder.fields(fields: _*)
    this
  }
  def minTermFreq(freq: Int) = {
    _builder.minTermFreq(freq)
    this
  }
  def stopWords(stopWords: String*) = {
    _builder.stopWords(stopWords: _*)
    this
  }
  def percentTermsToMatch(percentTermsToMatch: Double) = {
    _builder.percentTermsToMatch(percentTermsToMatch.toFloat)
    this
  }

  def maxWordLength(maxWordLen: Int) = {
    _builder.maxWordLength(maxWordLen)
    this
  }
  def minWordLength(minWordLen: Int) = {
    _builder.minWordLength(minWordLen)
    this
  }
  def boostTerms(boostTerms: Double) = {
    _builder.boostTerms(boostTerms.toFloat)
    this
  }
  def maxQueryTerms(maxQueryTerms: Int) = {
    _builder.maxQueryTerms(maxQueryTerms)
    this
  }
  def minDocFreq(minDocFreq: Int) = {
    _builder.minDocFreq(minDocFreq)
    this
  }
  def maxDocFreq(maxDocFreq: Int) = {
    _builder.maxDocFreq(maxDocFreq)
    this
  }

  def limit(sizeHint: Int) = size(sizeHint)

  def size(sizeHint: Int) = {
    _builder.searchSize(sizeHint)
    this
  }

  def start(i: Int) = from(i)
  def from(i: Int) = {
    _builder.searchFrom(i)
    this
  }
}
