package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

import scala.concurrent.duration.{Duration, FiniteDuration}

case class UpdateDefinition(indexAndTypes: IndexAndTypes,
                            id: String,
                            detectNoop: Option[Boolean] = None,
                            docAsUpsert: Option[Boolean] = None,
                            fetchSource: Option[FetchSourceContext] = None,
                            parent: Option[String] = None,
                            retryOnConflict: Option[Int] = None,
                            refresh: Option[RefreshPolicy] = None,
                            routing: Option[String] = None,
                            script: Option[ScriptDefinition] = None,
                            scriptedUpsert: Option[Boolean] = None,
                            timeout: Option[Duration] = None,
                            ttl: Option[String] = None,
                            version: Option[Long] = None,
                            versionType: Option[String] = None,
                            waitForActiveShards: Option[Int] = None,
                            upsertSource: Option[String] = None,
                            upsertFields: Map[String, Any] = Map.empty,
                            documentFields: Map[String, Any] = Map.empty,
                            documentSource: Option[String] = None) extends BulkCompatibleDefinition {
  require(indexAndTypes != null, "indexAndTypes must not be null or empty")
  require(id.toString.nonEmpty, "id must not be null or empty")

  // detects if a doc has not change and if so will not perform any action
  def detectNoop(detectNoop: Boolean): UpdateDefinition = copy(detectNoop = detectNoop.some)

  // Sets the object to use for updates when a script is not specified.
  def doc[T](t: T)(implicit indexable: Indexable[T]): UpdateDefinition = doc(indexable.json(t))
  def doc(doc: String): UpdateDefinition = copy(documentSource = doc.some)

  // Sets the fields to use for updates when a script is not specified.
  def doc(fields: (String, Any)*): UpdateDefinition = doc(fields.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(iterable: Iterable[(String, Any)]): UpdateDefinition = doc(iterable.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(map: Map[String, Any]): UpdateDefinition = copy(documentFields = map)

  // Sets the field to use for updates when a script is not specified.
  //def doc(value: FieldValue): UpdateDefinition = copy(documentSource = Seq(value))

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert[T: Indexable](t: T): UpdateDefinition = doc(t).copy(docAsUpsert = true.some)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(fields: (String, Any)*): UpdateDefinition = docAsUpsert(fields.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(iterable: Iterable[(String, Any)]): UpdateDefinition = docAsUpsert(iterable.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(map: Map[String, Any]): UpdateDefinition = doc(map).copy(docAsUpsert = true.some)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  //  def docAsUpsert(value: FieldValue): UpdateDefinition = {
  //    docAsUpsert(true)
  //    doc(value)
  //  }

  // should the doc be also used for a new document
  def docAsUpsert(shouldUpsertDoc: Boolean): UpdateDefinition = copy(docAsUpsert = shouldUpsertDoc.some)

  def fetchSource(fetch: Boolean): UpdateDefinition = copy(fetchSource = new FetchSourceContext(fetch).some)

  def fetchSource(includes: Iterable[String],
                  excludes: Iterable[String]): UpdateDefinition =
    copy(fetchSource = new FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def parent(parent: String): UpdateDefinition = copy(parent = parent.some)

  def routing(routing: String): UpdateDefinition = copy(routing = routing.some)

  def refresh(refresh: String): UpdateDefinition = copy(refresh = RefreshPolicy.valueOf(refresh).some)
  def refresh(refresh: RefreshPolicy): UpdateDefinition = copy(refresh = refresh.some)

  def retryOnConflict(retryOnConflict: Int): UpdateDefinition = copy(retryOnConflict = retryOnConflict.some)

  // executes this script as the update operation
  def script(script: ScriptDefinition): UpdateDefinition = copy(script = script.some)

  // If the document does not already exist, the script will be executed instead.
  def scriptedUpsert(upsert: Boolean): UpdateDefinition = copy(scriptedUpsert = upsert.some)

  def source[T: Indexable](t: T): UpdateDefinition = doc(t)
  def sourceAsUpsert[T: Indexable](t: T): UpdateDefinition = docAsUpsert(t)

  def timeout(duration: FiniteDuration): UpdateDefinition = copy(timeout = duration.some)

  def ttl(ttl: Long): UpdateDefinition = copy(ttl.toString)
  def ttl(ttl: String): UpdateDefinition = copy(ttl = ttl.some)

  // If the document does not already exist, the contents of the upsert element will be inserted as a new document.
  def upsert(map: Map[String, Any]): UpdateDefinition = copy(upsertFields = map)

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(fields: (String, Any)*): UpdateDefinition = upsert(fields.toMap)

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(iterable: Iterable[(String, Any)]): UpdateDefinition = upsert(iterable.toMap)

  def upsert[T](t: T)(implicit indexable: Indexable[T]): UpdateDefinition = upsert(indexable.json(t))
  def upsert(doc: String): UpdateDefinition = copy(upsertSource = doc.some)

  def versionType(versionType: String): UpdateDefinition = copy(versionType = versionType.some)
  def version(version: Long): UpdateDefinition = copy(version = version.some)
  def waitForActiveShards(waitForActiveShards: Int): UpdateDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)
}
