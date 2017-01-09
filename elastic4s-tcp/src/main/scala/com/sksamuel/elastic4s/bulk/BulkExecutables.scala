package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.index.IndexDefinitionExecutable
import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait BulkExecutables {

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, RichBulkResponse] {
    override def apply(c: Client, t: BulkDefinition): Future[RichBulkResponse] = {
      val builder = c.prepareBulk()
      t.timeout.foreach(builder.setTimeout)
      t.refresh.map(RefreshPolicy.valueOf).foreach(builder.setRefreshPolicy)
      t.requests.foreach {
        case index: IndexDefinition => builder.add(IndexDefinitionExecutable.builder(c, index))
        case delete: DeleteByIdDefinition => builder.add(delete.build)
        case update: UpdateDefinition => builder.add(update.build)
      }
      injectFutureAndMap(builder.execute)(RichBulkResponse.apply)
    }
  }
}
