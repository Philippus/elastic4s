package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition}
import com.sksamuel.elastic4s.{Executable, Show}
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait CreateIndexExecutables {

  implicit object CreateIndexDefinitionExecutable
    extends Executable[CreateIndexDefinition, CreateIndexResponse, CreateIndexResponse] {
    override def apply(c: Client, t: CreateIndexDefinition): Future[CreateIndexResponse] = {
      val req = CreateIndexBuilder(t)
      injectFuture(c.admin.indices.create(req, _))
    }
  }

  implicit object CreateIndexShow extends Show[CreateIndexDefinition] {
    override def show(f: CreateIndexDefinition): String = CreateIndexContentBuilder(f).string
  }

  implicit class CreateIndexShowOps(f: CreateIndexDefinition) {
    def show = CreateIndexShow.show(f)
  }
}
