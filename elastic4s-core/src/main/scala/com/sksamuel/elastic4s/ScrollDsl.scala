package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ScrollDsl {

  implicit object ScrollExecutable extends Executable[SearchScrollDefinition, SearchResponse, SearchResponse] {
    override def apply(client: Client, s: SearchScrollDefinition): Future[SearchResponse] = {
      val request = client.prepareSearchScroll(s.id)
      s._keepAlive.foreach(request.setScroll)
      injectFuture(request.execute)
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
