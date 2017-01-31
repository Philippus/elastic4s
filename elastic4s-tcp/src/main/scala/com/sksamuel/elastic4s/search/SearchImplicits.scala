package com.sksamuel.elastic4s.search

import cats.Show
import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches._
import org.elasticsearch.action.search.{MultiSearchResponse, SearchResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait SearchImplicits {

  implicit object SearchDefinitionExecutable
    extends Executable[SearchDefinition, SearchResponse, RichSearchResponse] {
    override def apply(c: Client, t: SearchDefinition): Future[RichSearchResponse] = {
      val builder = SearchBuilderFn(c, t)
      injectFutureAndMap(builder.execute)(RichSearchResponse.apply)
    }
  }

  //  implicit object SearchTemplateDefinitionExecutable
  //    extends Executable[SearchTemplateDefinition, SearchTemplateResponse, RichSearchTemplateResponse] {
  //    override def apply(client: Client, t: SearchTemplateDefinition): Future[RichSearchTemplateResponse] = {
  //      val builder = SearchTemplateAction.INSTANCE.newRequestBuilder(client)
  //      t.populate(builder)
  //      injectFutureAndMap(builder.execute)(RichSearchTemplateResponse.apply)
  //    }
  //  }

  implicit object MultiSearchDefinitionExecutable
    extends Executable[MultiSearchDefinition, MultiSearchResponse, RichMultiSearchResponse] {
    override def apply(c: Client, t: MultiSearchDefinition): Future[RichMultiSearchResponse] = {
      val builder = MultiSearchBuilderFn(c, t)
      injectFutureAndMap(builder.execute)(RichMultiSearchResponse.apply)
    }
  }

  implicit object SearchDefinitionShow extends Show[SearchDefinition] {
    override def show(f: SearchDefinition): String = f.toString
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
