package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexAdminDsl {
  implicit object OpenIndexDefinitionExecutable extends Executable[OpenIndexDefinition, OpenIndexResponse] {
    override def apply(c: Client, t: OpenIndexDefinition): Future[OpenIndexResponse] = {
      injectFuture(c.admin.indices.prepareOpen(t.index).execute)
    }
  }
  implicit object CloseIndexDefinitionExecutable extends Executable[CloseIndexDefinition, CloseIndexResponse] {
    override def apply(c: Client, t: CloseIndexDefinition): Future[CloseIndexResponse] = {
      injectFuture(c.admin.indices.prepareClose(t.index).execute)
    }
  }
}

class OpenIndexDefinition(val index: String)
class CloseIndexDefinition(val index: String)
