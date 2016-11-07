package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.elastic4s.{FieldsMapper, IndexAndTypes, Indexable, ProxyClients}
import com.sksamuel.elastic4s.definitions.DefinitionRouting
import com.sksamuel.elastic4s.mappings.FieldValue
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.action.update.{UpdateAction, UpdateRequest, UpdateRequestBuilder}
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.elasticsearch.index.VersionType

import scala.concurrent.duration.FiniteDuration

case class UpdateDefinition(indexAndTypes: IndexAndTypes, id: String)
  extends BulkCompatibleDefinition
    with DefinitionRouting {
  require(id.toString.nonEmpty, "id must not be null or empty")

  val _builder = new UpdateRequestBuilder(ProxyClients.client, UpdateAction.INSTANCE)
    .setIndex(indexAndTypes.index)
    .setType(indexAndTypes.types.headOption.orNull)
    .setId(id)

  def build: UpdateRequest = _builder.request

  private def _fieldsAsXContent(fields: Iterable[FieldValue]): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()
    fields.foreach(_.output(source))
    source.endObject()
  }

  // detects if a doc has not change and if so will not perform any action
  def detectNoop(detectNoop: Boolean): this.type = {
    _builder.setDetectNoop(detectNoop)
    this
  }

  // Sets the object to use for updates when a script is not specified.
  def doc[T](t: T)(implicit indexable: Indexable[T]): this.type = {
    _builder.setDoc(indexable.json(t))
    this
  }

  // Sets the fields to use for updates when a script is not specified.
  def doc(fields: (String, Any)*): this.type = doc(fields.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(iterable: Iterable[(String, Any)]): this.type = doc(iterable.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(map: Map[String, Any]): this.type = {
    _builder.setDoc(_fieldsAsXContent(FieldsMapper.mapFields(map)))
    this
  }

  def doc(source: String): this.type = {
    _builder.setDoc(source)
    this
  }

  // Sets the fields to use for updates when a script is not specified.
  def doc(source: XContentBuilder): this.type = {
    _builder.setDoc(source)
    this
  }

  // Sets the field to use for updates when a script is not specified.
  def doc(value: FieldValue): this.type = {
    _builder.setDoc(_fieldsAsXContent(Seq(value)))
    this
  }

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert[T: Indexable](t: T): this.type = {
    docAsUpsert(true)
    doc(t)
  }

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(fields: (String, Any)*): this.type = docAsUpsert(fields.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(iterable: Iterable[(String, Any)]): this.type = docAsUpsert(iterable.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(map: Map[String, Any]): this.type = {
    docAsUpsert(true)
    doc(map)
  }

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(value: FieldValue): this.type = {
    docAsUpsert(true)
    doc(value)
  }

  @deprecated("use docAsUpsert(false|true)", "5.0.0")
  def docAsUpsert: this.type = docAsUpsert(shouldUpsertDoc = true)

  // should the doc be also used for a new document
  def docAsUpsert(shouldUpsertDoc: Boolean): this.type = {
    _builder.setDocAsUpsert(shouldUpsertDoc: Boolean)
    this
  }

  def fetchSource(fetch: Boolean): this.type = {
    _builder.setFetchSource(fetch)
    this
  }

  def fetchSource(includes: Iterable[String], excludes: Iterable[String]): this.type = {
    _builder.setFetchSource(includes.toArray, excludes.toArray)
    this
  }

  @deprecated("use setFetchSource", "5.0.0")
  def fields(fs: String*): this.type = fields(fs)

  @deprecated("use setFetchSource", "5.0.0")
  def fields(fs: Iterable[String]): this.type = {
    _builder.setFields(fs.toSeq: _*)
    this
  }

  @deprecated("use setFetchSource", "5.0.0")
  def includeSource: this.type = {
    _builder.setFields("_source")
    this
  }

  def parent(parent: String): this.type = {
    _builder.setParent(parent)
    this
  }

  def refresh(refresh: RefreshPolicy): this.type = {
    _builder.setRefreshPolicy(refresh)
    this
  }

  def retryOnConflict(retryOnConflict: Int): this.type = {
    _builder.setRetryOnConflict(retryOnConflict)
    this
  }

  // executes this script as the update operation
  def script(script: ScriptDefinition): UpdateDefinition = {
    _builder.setScript(script.build)
    this
  }

  // If the document does not already exist, the script will be executed instead.
  def scriptedUpsert(upsert: Boolean): this.type = {
    _builder.setScriptedUpsert(upsert)
    this
  }

  def source[T: Indexable](t: T): this.type = doc(t)
  def sourceAsUpsert[T: Indexable](t: T): this.type = docAsUpsert(t)

  def timeout(duration: FiniteDuration): this.type = {
    _builder.setTimeout(TimeValue.timeValueMillis(duration.toMillis))
    this
  }

  def ttl(ttl: Long): this.type = {
    _builder.setTtl(ttl)
    this
  }

  def ttl(ttl: String): this.type = {
    _builder.setTtl(ttl)
    this
  }

  // If the document does not already exist, the contents of the upsert element will be inserted as a new document.
  def upsert(map: Map[String, Any]): this.type = {
    _builder.setUpsert(_fieldsAsXContent(FieldsMapper.mapFields(map)))
    this
  }

  // If the document does not already exist, the contents of the upsert element will be inserted as a new document.
  def upsert(source: XContentBuilder): UpdateDefinition = {
    _builder.setUpsert(source)
    this
  }

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(fields: (String, Any)*): this.type = upsert(fields.toMap)

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(iterable: Iterable[(String, Any)]): this.type = upsert(iterable.toMap)

  def versionType(versionType: VersionType): this.type = {
    _builder.setVersionType(versionType)
    this
  }

  def version(version: Long): this.type = {
    _builder.setVersion(version)
    this
  }

  def waitForActiveShards(count: Int): this.type = {
    _builder.setWaitForActiveShards(count)
    this
  }
}
