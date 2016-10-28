package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.search.{MultiSearchDefinition, MultiSearchResult, RescoreDefinition, SearchDefinition}
import org.elasticsearch.action.search._
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions


/** @author Stephen Samuel */
trait SearchDsl
  extends QueryDsl
  with HighlightDsl
  with ScriptFieldDsl
  with SuggestionDsl {

  implicit def toRichResponse(resp: SearchResponse): RichSearchResponse = RichSearchResponse(resp)

  def rescore(query: QueryDefinition): RescoreDefinition = {
    RescoreDefinition(query)
  }

  def multi(searches: Iterable[SearchDefinition]): MultiSearchDefinition = MultiSearchDefinition(searches)
  def multi(searches: SearchDefinition*): MultiSearchDefinition = MultiSearchDefinition(searches)

  implicit object SearchDefinitionExecutable
    extends Executable[SearchDefinition, SearchResponse, RichSearchResponse] {
    override def apply(c: Client, t: SearchDefinition): Future[RichSearchResponse] = {
      injectFutureAndMap(c.search(t.build, _))(RichSearchResponse.apply)
    }
  }

  implicit object MultiSearchDefinitionExecutable
    extends Executable[MultiSearchDefinition, MultiSearchResponse, MultiSearchResult] {
    override def apply(c: Client, t: MultiSearchDefinition): Future[MultiSearchResult] = {
      injectFutureAndMap(c.multiSearch(t.build, _))(MultiSearchResult.apply)
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