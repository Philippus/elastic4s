package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, DeleteByQueryAction}

import scala.concurrent.Future

trait DeleteExecutables {

  implicit object DeleteByIdDefinitionExecutable
    extends Executable[DeleteByIdDefinition, DeleteResponse, DeleteResponse] {
    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      injectFuture(c.delete(t.build, _))
    }
  }

  implicit object DeleteByQueryDefinitionExecutable
    extends Executable[DeleteByQueryDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(client: Client, d: DeleteByQueryDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
      d.populate(builder)
      injectFuture(builder.execute)
    }
  }
}
