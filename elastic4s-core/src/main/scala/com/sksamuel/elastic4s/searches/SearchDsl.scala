package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.script.ScriptFieldDsl
import org.elasticsearch.action.search._
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait SearchDsl
  extends QueryDsl
    with HighlightDsl
    with ScriptFieldDsl {

  def search(index: String): SearchDefinition = search(IndexesAndTypes(index))
  def search(first: String, rest: String*): SearchDefinition = search(first +: rest)
  def search(indexes: Iterable[String]): SearchDefinition = search(Indexes(indexes.toSeq))
  def search(indexes: Indexes): SearchDefinition = search(indexes.toIndexesAndTypes)
  def search(indexTypes: IndexAndTypes): SearchDefinition = search(indexTypes.toIndexesAndTypes)
  def search(indexesAndTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesAndTypes)

  def rescore(query: QueryDefinition) = RescoreDefinition(query)

  def multi(searches: Iterable[SearchDefinition]): MultiSearchDefinition = MultiSearchDefinition(searches)
  def multi(searches: SearchDefinition*): MultiSearchDefinition = MultiSearchDefinition(searches)

  implicit def toRichResponse(resp: SearchResponse): RichSearchResponse = RichSearchResponse(resp)

  implicit object SearchDefinitionExecutable
    extends Executable[SearchDefinition, SearchResponse, RichSearchResponse] {
    override def apply(c: Client, t: SearchDefinition): Future[RichSearchResponse] = {
      injectFutureAndMap(c.search(t.build, _))(RichSearchResponse.apply)
    }
  }

  implicit object MultiSearchDefinitionExecutable
    extends Executable[MultiSearchDefinition, MultiSearchResponse, RichMultiSearchResponse] {
    override def apply(c: Client, t: MultiSearchDefinition): Future[RichMultiSearchResponse] = {
      injectFutureAndMap(c.multiSearch(t.build, _))(RichMultiSearchResponse.apply)
    }
  }

  implicit object SearchDefinitionShow extends Show[SearchDefinition] {
    override def show(f: SearchDefinition): String = f._builder.toString
  }

  implicit class SearchDefinitionShowOps(f: SearchDefinition) {
    def show: String = SearchDefinitionShow.show(f)
  }

  implicit object MultiSearchDefinitionShow extends Show[MultiSearchDefinition] {

    import compat.Platform.EOL

    override def show(f: MultiSearchDefinition): String = f.searches.map(_.show).mkString("[" + EOL, "," + EOL, "]")
  }

  implicit class MultiSearchDefinitionShowOps(f: MultiSearchDefinition) {
    def show: String = MultiSearchDefinitionShow.show(f)
  }
}
