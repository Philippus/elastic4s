package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.DocumentSource
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }
import org.elasticsearch.script.ScriptService.ScriptType

/** @author Stephen Samuel */
trait UpdateDsl extends IndexesTypesDsl {

  def update = new UpdateExpectsId
  def update(id: Any) = new UpdateExpectsIndex(id.toString)
  class UpdateExpectsId {
    def id(id: Any) = new UpdateExpectsIndex(id.toString)
  }
  class UpdateExpectsIndex(id: String) {
    def in(indexesTypes: IndexesTypes): UpdateDefinition = new UpdateDefinition(indexesTypes, id)
  }

  class UpdateDefinition(indexesTypes: IndexesTypes, id: String) extends BulkCompatibleDefinition {

    val _builder = new UpdateRequestBuilder(ProxyClients.client)
      .setIndex(indexesTypes.index)
      .setType(indexesTypes.typ.orNull)
      .setId(id)

    def build = _builder.request

    private def _fieldsAsXContent(fields: Iterable[FieldValue]): XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      fields.foreach(_.output(source))
      source.endObject()
    }

    def script(script: String): UpdateDefinition = {
      _builder.setScript(script, ScriptType.INLINE)
      this
    }

    def detectNoop(detectNoop: Boolean): this.type = {
      _builder.setDetectNoop(detectNoop)
      this
    }

    def docAsUpsert(fields: (String, Any)*): UpdateDefinition = docAsUpsert(fields.toMap)
    def docAsUpsert(iterable: Iterable[(String, Any)]): UpdateDefinition = docAsUpsert(iterable.toMap)
    def docAsUpsert(map: Map[String, Any]): UpdateDefinition = {
      _builder.setDocAsUpsert(true)
      doc(map)
    }
    def doc(fields: (String, Any)*): UpdateDefinition = doc(fields.toMap)
    def doc(iterable: Iterable[(String, Any)]): UpdateDefinition = doc(iterable.toMap)
    def doc(map: Map[String, Any]): UpdateDefinition = {
      _builder.setDoc(_fieldsAsXContent(FieldsMapper.mapFields(map)))
      this
    }

    def doc(source: DocumentSource) = {
      _builder.setDoc(source.json)
      this
    }
    def routing(routing: String): UpdateDefinition = {
      _builder.setRouting(routing)
      this
    }
    def params(entries: (String, Any)*): UpdateDefinition = params(entries.toMap)
    def params(map: Map[String, Any]): UpdateDefinition = {
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
    def docAsUpsert: UpdateDefinition = docAsUpsert(shouldUpsertDoc = true)
    def docAsUpsert(shouldUpsertDoc: Boolean): UpdateDefinition = {
      _builder.setDocAsUpsert(shouldUpsertDoc: Boolean)
      this
    }
    def lang(scriptLang: String): UpdateDefinition = {
      _builder.setScriptLang(scriptLang)
      this
    }
    def upsert(map: Map[String, Any]): UpdateDefinition = {
      _builder.setUpsert(_fieldsAsXContent(FieldsMapper.mapFields(map)))
      this
    }
    def upsert(fields: (String, Any)*): UpdateDefinition = upsert(fields.toMap)
    def upsert(iterable: Iterable[(String, Any)]): UpdateDefinition = upsert(iterable.toMap)

    def scriptedUpsert(upsert: Boolean): this.type = {
      _builder.setScriptedUpsert(upsert)
      this
    }

    def version(version: Long): UpdateDefinition = {
      _builder.setVersion(version)
      this
    }
  }
}
