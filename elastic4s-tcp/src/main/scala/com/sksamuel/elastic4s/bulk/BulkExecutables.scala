package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.{EnumConversions, Executable}
import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteExecutables}
import com.sksamuel.elastic4s.index.IndexExecutables
import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.update.{UpdateDefinition, UpdateExecutables}
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait BulkExecutables {

  val execs = new IndexExecutables with UpdateExecutables with DeleteExecutables {}

  implicit object BulkDefinitionExecutable extends Executable[BulkDefinition, BulkResponse, RichBulkResponse] {
    override def apply(c: Client, t: BulkDefinition): Future[RichBulkResponse] = {
      val builder = c.prepareBulk()
      t.timeout.foreach(builder.setTimeout)
      t.refresh.map(EnumConversions.refreshPolicy).foreach(builder.setRefreshPolicy)
      t.requests.foreach {
        case index: IndexDefinition       => builder.add(execs.IndexDefinitionExecutable.builder(c, index))
        case delete: DeleteByIdDefinition => builder.add(execs.DeleteByIdDefinitionExecutable.builder(c, delete))
        case update: UpdateDefinition     => builder.add(execs.UpdateDefinitionExecutable.builder(c, update))
      }
      injectFutureAndMap(builder.execute)(RichBulkResponse.apply)
    }
  }
}
