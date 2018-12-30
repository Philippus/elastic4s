package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.searches.queries.Query

import scala.language.implicitConversions

trait SearchApi {

  def search(index: String): SearchRequest                = search(IndexesAndTypes(index))
  def search(first: String, rest: String*): SearchRequest = search(first +: rest)
  def search(index: Index): SearchRequest                 = search(index.name)
  def search(indexes: Iterable[String]): SearchRequest    = search(Indexes(indexes.toSeq))
  def search(indexes: Indexes): SearchRequest             = search(indexes.toIndexesAndTypes)

  @deprecated(
    "Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type. Therefore searching across multiple types is now deprecated because it will be removed in the next major release and you are encourged to move your code to use search without types. To remove this warning use the 6.0 method searchWithType()",
    "6.0"
  )
  def search(indexTypes: IndexAndTypes): SearchRequest         = search(indexTypes.toIndexesAndTypes)
  def searchWithType(indexTypes: IndexAndTypes): SearchRequest = search(indexTypes.toIndexesAndTypes)

  @deprecated(
    "Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type. Therefore searching across multiple types is now deprecated because it will be removed in the next major release and you are encourged to move your code to use search without types. To remove this warning use the 6.0 method searchWithType()",
    "6.0"
  )
  def search(indexesAndTypes: IndexesAndTypes): SearchRequest         = SearchRequest(indexesAndTypes)
  def searchWithType(indexesAndTypes: IndexesAndTypes): SearchRequest = SearchRequest(indexesAndTypes)

  def rescore(query: Query) = Rescore(query)

  def multi(searches: Iterable[SearchRequest]): MultiSearchRequest = MultiSearchRequest(searches)
  def multi(searches: SearchRequest*): MultiSearchRequest          = MultiSearchRequest(searches)
}
