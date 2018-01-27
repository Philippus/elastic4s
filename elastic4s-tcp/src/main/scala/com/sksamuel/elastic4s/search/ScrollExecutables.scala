package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches.{
  ClearScrollDefinition,
  ClearScrollResult,
  RichSearchResponse,
  SearchScrollDefinition
}
import org.elasticsearch.action.search.{ClearScrollResponse, SearchResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ScrollExecutables {

  implicit object ScrollExecutable extends Executable[SearchScrollDefinition, SearchResponse, RichSearchResponse] {
    override def apply(client: Client, s: SearchScrollDefinition): Future[RichSearchResponse] = {
      val request = client.prepareSearchScroll(s.id)
      s.keepAlive.foreach(request.setScroll)
      injectFutureAndMap(request.execute)(RichSearchResponse)
    }
  }

  implicit object ClearScrollDefinitionExecutable
      extends Executable[ClearScrollDefinition, ClearScrollResponse, ClearScrollResult] {
    override def apply(client: Client, s: ClearScrollDefinition): Future[ClearScrollResult] = {
      import scala.collection.JavaConverters._
      injectFutureAndMap(client.prepareClearScroll.setScrollIds(s.ids.asJava).execute)(resp => ClearScrollResult(resp))
    }
  }
}
