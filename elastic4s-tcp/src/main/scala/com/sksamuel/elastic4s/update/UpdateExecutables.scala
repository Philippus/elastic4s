package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, UpdateByQueryAction}

import scala.concurrent.Future

trait UpdateExecutables {

  implicit object UpdateByQueryDefinitionExecutable
    extends Executable[UpdateByQueryDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(c: Client, t: UpdateByQueryDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = UpdateByQueryAction.INSTANCE.newRequestBuilder(c)
      builder.source(t.sourceIndexes.values: _*)
      builder.filter(t.query.builder)
      t.requestsPerSecond.foreach(builder.setRequestsPerSecond)
      t.maxRetries.foreach(builder.setMaxRetries)
      t.refresh.foreach(builder.refresh)
      t.waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
      t.timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
      t.retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
      t.shouldStoreResult.foreach(builder.setShouldStoreResult)
      t.abortOnVersionConflict.foreach(builder.abortOnVersionConflict)
      t.pipeline.foreach(builder.setPipeline)
      t.script.map(_.build).foreach(builder.script)
      injectFuture(builder.execute)
    }
  }

  implicit object UpdateDefinitionExecutable
    extends Executable[UpdateDefinition, UpdateResponse, RichUpdateResponse] {
    override def apply(c: Client, t: UpdateDefinition): Future[RichUpdateResponse] = {
      injectFutureAndMap(c.update(t.build, _))(RichUpdateResponse.apply)
    }
  }
}
