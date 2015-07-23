package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.definitions.DefinitionRouting
import com.sksamuel.elastic4s.source.{Indexable, DocumentSource}
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.update.{UpdateRequest, UpdateRequestBuilder, UpdateResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.elasticsearch.index.VersionType

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/** @author Stephen Samuel */
trait UpdateDsl extends IndexesTypesDsl {

  implicit object UpdateDefinitionExecutable extends Executable[UpdateDefinition, UpdateResponse, UpdateResponse] {
    override def apply(c: Client, t: UpdateDefinition): Future[UpdateResponse] = {
      injectFuture(c.update(t.build, _))
    }
  }

  class UpdateExpectsIndex(id: String) {
    def in(indexType: IndexType): UpdateDefinition = in(IndexesTypes(indexType))
    def in(indexesTypes: IndexesTypes): UpdateDefinition = new UpdateDefinition(indexesTypes, id)
  }
}

class UpdateDefinition(indexesTypes: IndexesTypes, id: String)
  extends BulkCompatibleDefinition
  with DefinitionRouting {

  val _builder = new UpdateRequestBuilder(ProxyClients.client)
    .setIndex(indexesTypes.index)
    .setType(indexesTypes.typ.orNull)
    .setId(id)

  def build: UpdateRequest = _builder.request

  private def _fieldsAsXContent(fields: Iterable[FieldValue]): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()
    fields.foreach(_.output(source))
    source.endObject()
  }

  def detectNoop(detectNoop: Boolean): this.type = {
    _builder.setDetectNoop(detectNoop)
    this
  }

  def doc(fields: (String, Any)*): this.type = doc(fields.toMap)
  def doc(iterable: Iterable[(String, Any)]): this.type = doc(iterable.toMap)
  def doc(map: Map[String, Any]): this.type = {
    _builder.setDoc(_fieldsAsXContent(FieldsMapper.mapFields(map)))
    this
  }

  def doc(source: DocumentSource): UpdateDefinition = {
    _builder.setDoc(source.json)
    this
  }

  def doc(value: FieldValue): this.type = {
    _builder.setDoc(_fieldsAsXContent(Seq(value)))
    this
  }

  def docAsUpsert(fields: (String, Any)*): this.type = docAsUpsert(fields.toMap)
  def docAsUpsert(iterable: Iterable[(String, Any)]): this.type = docAsUpsert(iterable.toMap)
  def docAsUpsert(map: Map[String, Any]): this.type = {
    _builder.setDocAsUpsert(true)
    doc(map)
  }
  def docAsUpsert(value: FieldValue): this.type = {
    _builder.setDocAsUpsert(true)
    doc(value)
  }

  def docAsUpsert: UpdateDefinition = docAsUpsert(shouldUpsertDoc = true)
  def docAsUpsert(shouldUpsertDoc: Boolean): this.type = {
    _builder.setDocAsUpsert(shouldUpsertDoc: Boolean)
    this
  }

  def includeSource: this.type = {
    _builder.setFields("_source")
    this
  }

  def fields(fs: String*): this.type = fields(fs)
  def fields(fs: Iterable[String]): this.type = {
    _builder.setFields(fs.toSeq: _*)
    this
  }

  def params(entries: (String, Any)*): this.type = params(entries.toMap)
  def params(map: Map[String, Any]): this.type = {
    map.foreach(arg => _builder.addScriptParam(arg._1, arg._2))
    this
  }

  def retryOnConflict(retryOnConflict: Int): this.type = {
    _builder.setRetryOnConflict(retryOnConflict)
    this
  }

  def parent(parent: String): this.type = {
    _builder.setParent(parent)
    this
  }

  def refresh(refresh: Boolean): this.type = {
    _builder.setRefresh(refresh)
    this
  }

  @deprecated("will be removed in 2.0.0. See https://github.com/elastic/elasticsearch/pull/10171", "1.6.0")
  def replicationType(repType: ReplicationType): this.type = {
    _builder.setReplicationType(repType)
    this
  }

  def consistencyLevel(consistencyLevel: WriteConsistencyLevel): this.type = {
    _builder.setConsistencyLevel(consistencyLevel)
    this
  }

  def timeout(duration: FiniteDuration): this.type = {
    _builder.setTimeout(TimeValue.timeValueMillis(duration.toMillis))
    this
  }

  def lang(scriptLang: String): this.type = {
    _builder.setScriptLang(scriptLang)
    this
  }

  def source[T](t: T)(implicit indexable: Indexable[T]): this.type = {
    _builder.setDoc(indexable.json(t))
    this
  }

  def sourceAsUpsert[T](t: T)(implicit indexable: Indexable[T]): this.type = {
    source(t)
    docAsUpsert(true)
    this
  }

  def upsert(map: Map[String, Any]): this.type = {
    _builder.setUpsert(_fieldsAsXContent(FieldsMapper.mapFields(map)))
    this
  }

  def script(script: String, scriptType: ScriptType = ScriptType.Inline): UpdateDefinition = {
    _builder.setScript(script, scriptType.elasticType)
    this
  }

  def scriptedUpsert(upsert: Boolean): this.type = {
    _builder.setScriptedUpsert(upsert)
    this
  }

  def upsert(fields: (String, Any)*): this.type = upsert(fields.toMap)
  def upsert(iterable: Iterable[(String, Any)]): this.type = upsert(iterable.toMap)

  def versionType(versionType: VersionType): this.type = {
    _builder.setVersionType(versionType)
    this
  }

  def version(version: Long): this.type = {
    _builder.setVersion(version)
    this
  }
}
