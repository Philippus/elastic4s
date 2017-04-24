package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.{Executable, ScriptBuilder}
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.{ReindexAction, ReindexRequestBuilder}

import scala.concurrent.Future

trait ReindexExecutables {
  implicit object ReindexDefinitionExecutable
    extends Executable[ReindexDefinition, BulkByScrollResponse, BulkByScrollResponse] {

    def populate(builder: ReindexRequestBuilder, r: ReindexDefinition): Unit = {
      builder.source(r.sourceIndexes.values: _*)
      r.targetType.fold(builder.destination(r.targetIndex))(builder.destination(r.targetIndex, _))
      r.filter.map(QueryBuilderFn.apply).foreach(builder.filter)
      r.timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
      r.requestsPerSecond.foreach(builder.setRequestsPerSecond)
      r.maxRetries.foreach(builder.setMaxRetries)
      r.refresh.foreach(builder.refresh)
      r.waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
      r.retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
      r.shouldStoreResult.foreach(builder.setShouldStoreResult)
      r.size.foreach(builder.size)
      r.script.map(ScriptBuilder.apply).foreach(builder.script)
    }

    override def apply(c: Client, r: ReindexDefinition): Future[BulkByScrollResponse] = {
      val builder = new ReindexRequestBuilder(c, ReindexAction.INSTANCE)
      populate(builder, r)
      injectFuture(builder.execute)
    }
  }
}
