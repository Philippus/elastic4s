package com.sksamuel.elastic4s

import org.elasticsearch.action.search.{ClearScrollResponse, SearchResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ScrollDsl {

  implicit object ScrollExecutable extends Executable[SearchScrollDefinition, SearchResponse] {
    override def apply(client: Client, s: SearchScrollDefinition): Future[SearchResponse] = {
      val request = client.prepareSearchScroll(s.id)
      s._keepAlive.foreach(request.setScroll)
      injectFuture(request.execute)
    }
  }

  implicit object ClearScrollDefinitionExecutable
    extends Executable[ClearScrollDefinition, ClearScrollResponse] {
    override def apply(client: Client, s: ClearScrollDefinition): Future[ClearScrollResponse] = {
      import scala.collection.JavaConverters._
      injectFuture(client.prepareClearScroll.setScrollIds(s.ids.asJava).execute)
    }
  }
}

class SearchScrollDefinition(val id: String) {

  private[elastic4s] var _keepAlive: Option[String] = None

  def keepAlive(time: String): this.type = {
    _keepAlive = Option(time)
    this
  }
}

case class ClearScrollDefinition(ids: Seq[String])
case class ClearScrollResult(response: ClearScrollResponse) {
  def number = response.getNumFreed
  def success = response.isSucceeded
}