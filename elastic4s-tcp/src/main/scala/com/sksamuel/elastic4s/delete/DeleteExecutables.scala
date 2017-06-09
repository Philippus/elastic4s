package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse
import org.elasticsearch.action.delete.{DeleteRequestBuilder, DeleteResponse}
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.{DeleteByQueryAction, DeleteByQueryRequestBuilder}

import scala.concurrent.Future

trait DeleteExecutables {

  implicit object DeleteByIdDefinitionExecutable
    extends Executable[DeleteByIdDefinition, DeleteResponse, DeleteResponse] {

    def builder(c: Client, t: DeleteByIdDefinition): DeleteRequestBuilder = {
      val _builder = c.prepareDelete().setIndex(t.indexType.index).setType(t.indexType.`type`).setId(t.id.toString)
      t.routing.foreach(_builder.setRouting)
      t.refresh.foreach(_builder.setRefreshPolicy)
      t.parent.foreach(_builder.setParent)
      t.waitForActiveShards.foreach(_builder.setWaitForActiveShards)
      t.version.foreach(_builder.setVersion)
      t.versionType.foreach(_builder.setVersionType)
      _builder
    }

    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      val _builder = builder(c, t)
      injectFuture(_builder.execute)
    }
  }

  implicit object DeleteByQueryDefinitionExecutable
    extends Executable[DeleteByQueryDefinition, BulkByScrollResponse, BulkByScrollResponse] {

    def populate(builder: DeleteByQueryRequestBuilder, d: DeleteByQueryDefinition): Unit = {
      builder.source(d.indexesAndTypes.indexes: _*)
      if (d.indexesAndTypes.types.nonEmpty)
        builder.source().setTypes(d.indexesAndTypes.types: _*)
      builder.filter(QueryBuilderFn(d.query))
      d.requestsPerSecond.foreach(builder.setRequestsPerSecond)
      d.size.foreach(builder.size)
      d.maxRetries.foreach(builder.setMaxRetries)
      if (d.refresh.contains(RefreshPolicy.IMMEDIATE))
        builder.refresh(true)
      d.waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
      d.timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
      d.retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
      d.shouldStoreResult.foreach(builder.setShouldStoreResult)
      d.abortOnVersionConflict.foreach(builder.abortOnVersionConflict)
    }

    override def apply(client: Client, d: DeleteByQueryDefinition): Future[BulkByScrollResponse] = {
      val builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
      populate(builder, d)
      injectFuture(builder.execute)
    }
  }
}
