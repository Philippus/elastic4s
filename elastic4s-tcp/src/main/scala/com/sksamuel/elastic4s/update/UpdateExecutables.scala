package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.action.update.{UpdateRequestBuilder, UpdateResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.VersionType
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

    def builder(c: Client, t: UpdateDefinition): UpdateRequestBuilder = {
      val _builder = c.prepareUpdate(t.indexAndTypes.index, t.indexAndTypes.types.headOption.orNull, t.id)
      t.fetchSource.foreach { context =>
        _builder.setFetchSource(context.enabled)
        if (context.includes.nonEmpty || context.excludes.nonEmpty)
          _builder.setFetchSource(context.includes.toArray, context.excludes.toArray)
      }
      t.detectNoop.foreach(_builder.setDetectNoop)
      t.docAsUpsert.foreach(_builder.setDocAsUpsert)
      t.upsert.foreach(_builder.setUpsert)
      t.document.foreach(_builder.setDoc)
      t.routing.foreach(_builder.setRouting)
      t.refresh.foreach(_builder.setRefreshPolicy)
      t.ttl.foreach(_builder.setTtl)
      t.timeout.map(dur => TimeValue.timeValueMillis(dur.toMillis)).foreach(_builder.setTimeout)
      t.retryOnConflict.foreach(_builder.setRetryOnConflict)
      t.waitForActiveShards.foreach(_builder.setWaitForActiveShards)
      t.parent.foreach(_builder.setParent)
      t.script.map(_.build).foreach(_builder.setScript)
      t.scriptedUpsert.foreach(_builder.setScriptedUpsert)
      t.version.foreach(_builder.setVersion)
      t.versionType.map(VersionType.fromString).foreach(_builder.setVersionType)
      _builder
    }

    override def apply(c: Client, t: UpdateDefinition): Future[RichUpdateResponse] = {
      val _builder = builder(c, t)
      injectFutureAndMap(_builder.execute)(RichUpdateResponse.apply)
    }
  }
}
