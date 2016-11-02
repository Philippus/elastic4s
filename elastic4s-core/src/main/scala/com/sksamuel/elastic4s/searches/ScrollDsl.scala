package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.search.{ClearScrollResponse, SearchResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait ScrollDsl {

  def searchScroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)

  def clearScroll(id: String, ids: String*): ClearScrollDefinition = clearScroll(id +: ids)
  def clearScroll(ids: Iterable[String]): ClearScrollDefinition = ClearScrollDefinition(ids.toSeq)

  implicit object ScrollExecutable extends Executable[SearchScrollDefinition, SearchResponse, RichSearchResponse] {
    override def apply(client: Client, s: SearchScrollDefinition): Future[RichSearchResponse] = {
      val request = client.prepareSearchScroll(s.id)
      s._keepAlive.foreach(request.setScroll)
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

case class SearchScrollDefinition(id: String) {

  private[elastic4s] var _keepAlive: Option[String] = None

  def keepAlive(time: String): this.type = {
    _keepAlive = Option(time)
    this
  }

  def keepAlive(duration: FiniteDuration): this.type = {
    _keepAlive = Option(duration.toSeconds + "s")
    this
  }
}

case class ClearScrollDefinition(ids: Seq[String])
case class ClearScrollResult(response: ClearScrollResponse) {
  def number = response.getNumFreed
  def success = response.isSucceeded
}
