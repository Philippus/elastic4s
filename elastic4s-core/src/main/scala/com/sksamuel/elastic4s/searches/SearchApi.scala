package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

import scala.language.implicitConversions

trait SearchApi {

  def search(index: String): SearchDefinition = search(IndexesAndTypes(index))
  def search(first: String, rest: String*): SearchDefinition = search(first +: rest)
  def search(indexes: Iterable[String]): SearchDefinition = search(Indexes(indexes.toSeq))
  def search(indexes: Indexes): SearchDefinition = search(indexes.toIndexesAndTypes)


  @deprecated("Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type. Therefore searching across multiple types is now deprecated because types will no longer work in the next release.", "6.0")
  def search(indexTypes: IndexAndTypes): SearchDefinition = search(indexTypes.toIndexesAndTypes)

  @deprecated("Elasticsearch 6.0 has deprecated types with the intention of removing them in 7.0. You can continue to use them in existing indexes, but all new indexes must only have a single type. Therefore searching across multiple types is now deprecated because types will no longer work in the next release.", "6.0")
  def search(indexesAndTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesAndTypes)

  def rescore(query: QueryDefinition) = RescoreDefinition(query)

  def multi(searches: Iterable[SearchDefinition]): MultiSearchDefinition = MultiSearchDefinition(searches)
  def multi(searches: SearchDefinition*): MultiSearchDefinition = MultiSearchDefinition(searches)
}
