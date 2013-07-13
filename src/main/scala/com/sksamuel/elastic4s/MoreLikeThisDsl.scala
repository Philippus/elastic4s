package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait MoreLikeThisDsl {

  def mlt = new MltExpectingId
  def morelike = new MltExpectingId
  class MltExpectingId {
    def id(id: Any) = new MltExpectsIndex(id.toString)
  }

  class MltExpectsIndex(id: String) {
    def in(in: String) = in.split("/").toList match {
      case idx :: Nil => new MoreLikeThisDefinition(idx, null, id)
      case idx :: t :: Nil => new MoreLikeThisDefinition(idx, t, id)
      case _ => throw new RuntimeException
    }
  }

  class MoreLikeThisDefinition(index: String, `type`: String, id: String) {
    val _builder = Requests.moreLikeThisRequest(index).`type`(`type`).id(id)
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
    def maxWordLen(maxWordLen: Int) = {
      _builder.maxWordLen(maxWordLen)
      this
    }
    def minWordLen(minWordLen: Int) = {
      _builder.minWordLen(minWordLen)
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
  }
}
