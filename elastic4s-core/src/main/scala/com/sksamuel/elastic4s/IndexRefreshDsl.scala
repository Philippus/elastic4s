package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.refresh.{RefreshRequest, RefreshResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexRefreshDsl {

  implicit object IndexRefreshDefinitionExecutable
    extends Executable[IndexRefreshDefinition, RefreshResponse, RefreshResponse] {
    override def apply(c: Client, t: IndexRefreshDefinition): Future[RefreshResponse] = {
      injectFuture(c.admin.indices.refresh(t.build, _))
    }
  }
}

class IndexRefreshDefinition(indices: Seq[String]) {
  private def builder = new RefreshRequest(indices: _*)
  def build = builder
}