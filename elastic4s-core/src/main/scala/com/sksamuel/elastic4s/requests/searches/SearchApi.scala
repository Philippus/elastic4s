package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.searches.queries.Query

import scala.language.implicitConversions

trait SearchApi {

  def search(index: String): SearchRequest                = search(Indexes(index))
  def search(first: String, rest: String*): SearchRequest = search(first +: rest)
  def search(index: Index): SearchRequest                 = search(index.name)
  def search(indexes: Iterable[String]): SearchRequest    = search(Indexes(indexes.toSeq))
  def search(indexes: Indexes): SearchRequest             = SearchRequest(indexes)

  def rescore(query: Query) = Rescore(query)

  def multi(searches: Iterable[SearchRequest]): MultiSearchRequest = MultiSearchRequest(searches)
  def multi(searches: SearchRequest*): MultiSearchRequest          = MultiSearchRequest(searches)
}
