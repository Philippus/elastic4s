package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

import scala.language.implicitConversions

trait SearchApi {
  def search(index: String): SearchDefinition = search(IndexesAndTypes(index))
  def search(first: String, rest: String*): SearchDefinition = search(first +: rest)
  def search(indexes: Iterable[String]): SearchDefinition = search(Indexes(indexes.toSeq))
  def search(indexes: Indexes): SearchDefinition = search(indexes.toIndexesAndTypes)
  def search(indexTypes: IndexAndTypes): SearchDefinition = search(indexTypes.toIndexesAndTypes)
  def search(indexesAndTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesAndTypes)

  def rescore(query: QueryDefinition) = RescoreDefinition(query)

  def multi(searches: Iterable[SearchDefinition]): MultiSearchDefinition = MultiSearchDefinition(searches)
  def multi(searches: SearchDefinition*): MultiSearchDefinition = MultiSearchDefinition(searches)
}
