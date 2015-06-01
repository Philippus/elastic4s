package com.sksamuel.elastic4s

import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.search.Scroll

import scala.concurrent.Future

/** @author Stephen Samuel */
trait MoreLikeThisDsl {

  class MltExpectsIndex(id: String) {

    def in(indexType: IndexType): MoreLikeThisDefinition = new MoreLikeThisDefinition(indexType.index, indexType.`type`, id)

    def in(in: String): MoreLikeThisDefinition = in.split("/").toList match {
      case indx :: Nil => new MoreLikeThisDefinition(indx, null, id)
      case indx :: t :: Nil => new MoreLikeThisDefinition(indx, t, id)
      case _ => throw new RuntimeException
    }
  }

  implicit object MoreLikeThisDefinitionExecutable extends Executable[MoreLikeThisDefinition, SearchResponse] {
    override def apply(c: Client, t: MoreLikeThisDefinition): Future[SearchResponse] = {
      injectFuture(c.moreLikeThis(t.build, _))
    }
  }
}

class MoreLikeThisDefinition(index: String, `type`: String, id: String) {

  private val _builder = Requests.moreLikeThisRequest(index).`type`(`type`).id(id)
  def build: MoreLikeThisRequest = _builder

  def boostTerms(boostTerms: Double): this.type = {
    _builder.boostTerms(boostTerms.toFloat)
    this
  }

  def fields(_fields: String*): this.type = fields(_fields)

  def fields(_fields: Iterable[String]): this.type = {
    _builder.fields(_fields.toSeq: _*)
    this
  }

  def filter(query: FilterDefinition): this.type = {
    _builder.searchSource(query.builder.buildAsBytes.array)
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
