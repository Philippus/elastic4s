package com.sksamuel.elastic4s

import org.elasticsearch.action.update.{UpdateAction, UpdateRequestBuilder}
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.source.Source

/** @author Stephen Samuel */
trait UpdateDsl {

  def update = new UpdateExpectsId
  def update(id: Any) = new UpdateExpectsIndex(id.toString)
  class UpdateExpectsId {
    def id(id: Any) = new UpdateExpectsIndex(id.toString)
  }
  class UpdateExpectsIndex(id: String) {
    def in(index: String) = new UpdateDefinition(index, id)
  }

  class UpdateDefinition(index: String, id: String)
    extends RequestDefinition(UpdateAction.INSTANCE) with BulkCompatibleDefinition {

    val _builder = new UpdateRequestBuilder(null)
      .setIndex(index.split("/").head)
      .setType(index.split("/").last)
      .setId(id)

    def build = _builder.request

    private def fieldsAsXContent(fields: Iterable[(String, Any)]): XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      for ( tuple <- fields ) {
        source.field(tuple._1, tuple._2)
      }
      source.endObject()
    }

    def script(script: String): UpdateDefinition = {
      _builder.setScript(script)
      this
    }

    def docAsUpsert(map: Map[String, Any]): UpdateDefinition = docAsUpsert(map.toList)
    def docAsUpsert(fields: (String, Any)*): UpdateDefinition = docAsUpsert(fields.toIterable)
    def docAsUpsert(iterable: Iterable[(String, Any)]): UpdateDefinition = {
      _builder.setDocAsUpsert(true)
      doc(iterable)
    }
    def doc(map: Map[String, Any]): UpdateDefinition = doc(map.toList)
    def doc(fields: (String, Any)*): UpdateDefinition = doc(fields.toIterable)
    def doc(iterable: Iterable[(String, Any)]): UpdateDefinition = {
      _builder.setDoc(fieldsAsXContent(iterable))
      this
    }
    def doc(source: Source) = {
      _builder.setDoc(source.json)
      this
    }
    def routing(routing: String): UpdateDefinition = {
      _builder.setRouting(routing)
      this
    }
    def params(map: Map[String, AnyRef]): UpdateDefinition = {
      map.foreach(arg => _builder.addScriptParam(arg._1, arg._2))
      this
    }
    def retryOnConflict(retryOnConflict: Int): UpdateDefinition = {
      _builder.setRetryOnConflict(retryOnConflict)
      this
    }
    def parent(parent: String): UpdateDefinition = {
      _builder.setParent(parent)
      this
    }
    def refresh(refresh: Boolean): UpdateDefinition = {
      _builder.setRefresh(refresh)
      this
    }
    def replicationType(repType: ReplicationType): UpdateDefinition = {
      _builder.setReplicationType(repType)
      this
    }
    def consistencyLevel(consistencyLevel: WriteConsistencyLevel): UpdateDefinition = {
      _builder.setConsistencyLevel(consistencyLevel)
      this
    }
    def percolate(percolate: String): UpdateDefinition = {
      _builder.setPercolate(percolate)
      this
    }
    def docAsUpsert: UpdateDefinition = docAsUpsert(shouldUpsertDoc = true)
    def docAsUpsert(shouldUpsertDoc: Boolean): UpdateDefinition = {
      _builder.setDocAsUpsert(shouldUpsertDoc: Boolean)
      this
    }
    def lang(scriptLang: String): UpdateDefinition = {
      _builder.setScriptLang(scriptLang)
      this
    }
    def upsert(map: Map[String, Any]): UpdateDefinition = upsert(map.toList)
    def upsert(fields: (String, Any)*): UpdateDefinition = upsert(fields.toIterable)
    def upsert(iterable: Iterable[(String, Any)]): UpdateDefinition = {
      _builder.setUpsert(fieldsAsXContent(iterable))
      this
    }
  }
}
