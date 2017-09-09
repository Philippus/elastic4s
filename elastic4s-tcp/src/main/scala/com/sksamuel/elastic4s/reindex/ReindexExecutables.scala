package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.{Executable, ScriptBuilder}
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.reindex.{BulkByScrollResponse, ReindexAction, ReindexRequestBuilder}

import scala.concurrent.Future

trait ReindexExecutables {

  implicit object ReindexDefinitionExecutable
    extends Executable[ReindexDefinition, BulkByScrollResponse, BulkByScrollResponse] {

    def populate(builder: ReindexRequestBuilder, r: ReindexDefinition): Unit = {
      builder.source(r.sourceIndexes.values: _*)
      r.targetType.fold(builder.destination(r.targetIndex))(builder.destination(r.targetIndex, _))
      r.filter.map(QueryBuilderFn.apply).foreach(builder.filter)
      r.maxRetries.foreach(builder.setMaxRetries)
      r.retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
      r.shouldStoreResult.foreach(builder.setShouldStoreResult)
      r.size.foreach(builder.size)
      r.script.map(ScriptBuilder.apply).foreach(builder.script)

      r.urlParams.timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
      r.urlParams.requestsPerSecond.foreach(builder.setRequestsPerSecond)
      r.urlParams.refresh.foreach(refreshPolicy =>
        builder.refresh(refreshPolicy match {
          case RefreshPolicy.NONE => false
          case _ => true
        }))
      r.urlParams.waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
    }

    override def apply(c: Client, r: ReindexDefinition): Future[BulkByScrollResponse] = {
      val builder = new ReindexRequestBuilder(c, ReindexAction.INSTANCE)
      populate(builder, r)
      injectFuture(builder.execute)
    }
  }

}
