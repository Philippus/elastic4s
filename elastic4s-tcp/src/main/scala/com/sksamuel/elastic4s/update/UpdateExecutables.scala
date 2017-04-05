package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s._
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse
import org.elasticsearch.action.support.ActiveShardCount
import org.elasticsearch.action.update.{UpdateRequestBuilder, UpdateResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.elasticsearch.index.VersionType
import org.elasticsearch.index.reindex.UpdateByQueryAction

import scala.concurrent.Future

trait UpdateExecutables {

  implicit object UpdateByQueryDefinitionExecutable
    extends Executable[UpdateByQueryDefinition, BulkByScrollResponse, BulkByScrollResponse] {

    override def apply(c: Client, t: UpdateByQueryDefinition): Future[BulkByScrollResponse] = {
      val builder = UpdateByQueryAction.INSTANCE.newRequestBuilder(c)
      builder.source(t.sourceIndexes.values: _*)
      builder.filter(QueryBuilderFn(t.query))
      t.requestsPerSecond.foreach(builder.setRequestsPerSecond)
      t.maxRetries.foreach(builder.setMaxRetries)
      t.refresh.foreach(builder.refresh)
      t.waitForActiveShards.map(ActiveShardCount.from).foreach(builder.waitForActiveShards)
      t.timeout.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.timeout)
      t.retryBackoffInitialTime.map(_.toNanos).map(TimeValue.timeValueNanos).foreach(builder.setRetryBackoffInitialTime)
      t.shouldStoreResult.foreach(builder.setShouldStoreResult)
      t.abortOnVersionConflict.foreach(builder.abortOnVersionConflict)
      t.pipeline.foreach(builder.setPipeline)
      t.script.map(ScriptBuilder.apply).foreach(builder.script)
      injectFuture(builder.execute)
    }
  }

  implicit object UpdateDefinitionExecutable
    extends Executable[UpdateDefinition, UpdateResponse, RichUpdateResponse] {

    def fieldsAsXContent(fields: Iterable[FieldValue]): XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      fields.foreach(XContentFieldValueWriter(source, _))
      source.endObject()
    }

    def builder(c: Client, t: UpdateDefinition): UpdateRequestBuilder = {

      val builder = c.prepareUpdate(t.indexAndTypes.index, t.indexAndTypes.types.headOption.orNull, t.id)

      t.fetchSource.foreach { context =>
        builder.setFetchSource(context.fetchSource)
        if (context.includes.nonEmpty || context.excludes.nonEmpty)
          builder.setFetchSource(context.includes, context.excludes)
      }

      t.detectNoop.foreach(builder.setDetectNoop)
      t.docAsUpsert.foreach(builder.setDocAsUpsert)

      t.upsertSource.foreach(builder.setUpsert)
      t.documentSource.foreach(builder.setDoc)

      if (t.upsertFields.nonEmpty) {
        builder.setUpsert(fieldsAsXContent(FieldsMapper.mapFields(t.upsertFields)))
      }

      if (t.documentFields.nonEmpty) {
        builder.setDoc(fieldsAsXContent(FieldsMapper.mapFields(t.documentFields)))
      }

      t.routing.foreach(builder.setRouting)
      t.refresh.foreach(builder.setRefreshPolicy)
      t.ttl.foreach(builder.setTtl)
      t.timeout.map(dur => TimeValue.timeValueMillis(dur.toMillis)).foreach(builder.setTimeout)
      t.retryOnConflict.foreach(builder.setRetryOnConflict)
      t.waitForActiveShards.foreach(builder.setWaitForActiveShards)
      t.parent.foreach(builder.setParent)
      t.script.map(ScriptBuilder.apply).foreach(builder.setScript)
      t.scriptedUpsert.foreach(builder.setScriptedUpsert)
      t.version.foreach(builder.setVersion)
      t.versionType.map(VersionType.fromString).foreach(builder.setVersionType)
      builder
    }

    override def apply(c: Client, t: UpdateDefinition): Future[RichUpdateResponse] = {
      val _builder = builder(c, t)
      injectFutureAndMap(_builder.execute)(RichUpdateResponse.apply)
    }
  }
}
