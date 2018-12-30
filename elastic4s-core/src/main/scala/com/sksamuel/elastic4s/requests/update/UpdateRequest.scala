package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import com.sksamuel.elastic4s.requests.common.{FetchSourceContext, RefreshPolicy}
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.{Duration, FiniteDuration}

case class UpdateRequest(indexAndType: IndexAndType,
                         id: String,
                         detectNoop: Option[Boolean] = None,
                         docAsUpsert: Option[Boolean] = None,
                         fetchSource: Option[FetchSourceContext] = None,
                         parent: Option[String] = None,
                         retryOnConflict: Option[Int] = None,
                         refresh: Option[RefreshPolicy] = None,
                         routing: Option[String] = None,
                         script: Option[Script] = None,
                         scriptedUpsert: Option[Boolean] = None,
                         timeout: Option[Duration] = None,
                         version: Option[Long] = None,
                         versionType: Option[String] = None,
                         waitForActiveShards: Option[Int] = None,
                         upsertSource: Option[String] = None,
                         upsertFields: Map[String, Any] = Map.empty,
                         documentFields: Map[String, Any] = Map.empty,
                         documentSource: Option[String] = None)
    extends BulkCompatibleRequest {
  require(indexAndType != null, "indexAndTypes must not be null or empty")
  require(id.toString.nonEmpty, "id must not be null or empty")

  // detects if a doc has not change and if so will not perform any action
  def detectNoop(detectNoop: Boolean): UpdateRequest = copy(detectNoop = detectNoop.some)

  // Sets the object to use for updates when a script is not specified.
  def doc[T](t: T)(implicit indexable: Indexable[T]): UpdateRequest = doc(indexable.json(t))
  def doc(doc: String): UpdateRequest                               = copy(documentSource = doc.some)

  // Sets the fields to use for updates when a script is not specified.
  def doc(fields: (String, Any)*): UpdateRequest = doc(fields.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(iterable: Iterable[(String, Any)]): UpdateRequest = doc(iterable.toMap)

  // Sets the fields to use for updates when a script is not specified.
  def doc(map: Map[String, Any]): UpdateRequest = copy(documentFields = map)

  // Sets the field to use for updates when a script is not specified.
  //def doc(value: FieldValue): UpdateDefinition = copy(documentSource = Seq(value))

  def docAsUpsert(json: String): UpdateRequest = doc(json).copy(docAsUpsert = true.some)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert[T: Indexable](t: T): UpdateRequest = doc(t).copy(docAsUpsert = true.some)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(fields: (String, Any)*): UpdateRequest = docAsUpsert(fields.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(iterable: Iterable[(String, Any)]): UpdateRequest = docAsUpsert(iterable.toMap)

  // Uses this document as both the update value and for creating a new doc if the doc does not already exist
  def docAsUpsert(map: Map[String, Any]): UpdateRequest = doc(map).copy(docAsUpsert = true.some)

  // should the doc be also used for a new document
  def docAsUpsert(shouldUpsertDoc: Boolean): UpdateRequest = copy(docAsUpsert = shouldUpsertDoc.some)

  def fetchSource(fetch: Boolean): UpdateRequest = copy(fetchSource = FetchSourceContext(fetch).some)

  def fetchSource(includes: Iterable[String], excludes: Iterable[String]): UpdateRequest =
    copy(fetchSource = FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def parent(parent: String): UpdateRequest = copy(parent = parent.some)

  def routing(routing: String): UpdateRequest = copy(routing = routing.some)

  def refresh(refresh: RefreshPolicy): UpdateRequest = copy(refresh = refresh.some)
  def refreshImmediately: UpdateRequest = refresh(RefreshPolicy.IMMEDIATE)

  def retryOnConflict(retryOnConflict: Int): UpdateRequest = copy(retryOnConflict = retryOnConflict.some)

  // executes this script as the update operation
  def script(script: Script): UpdateRequest = copy(script = script.some)

  // If the document does not already exist, the script will be executed instead.
  def scriptedUpsert(upsert: Boolean): UpdateRequest = copy(scriptedUpsert = upsert.some)

  def source[T: Indexable](t: T): UpdateRequest         = doc(t)
  def sourceAsUpsert[T: Indexable](t: T): UpdateRequest = docAsUpsert(t)

  def timeout(duration: FiniteDuration): UpdateRequest = copy(timeout = duration.some)

  // If the document does not already exist, the contents of the upsert element will be inserted as a new document.
  def upsert(map: Map[String, Any]): UpdateRequest = copy(upsertFields = map)

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(fields: (String, Any)*): UpdateRequest = upsert(fields.toMap)

  // If the document does not already exist, the contents of the upsert fields will be inserted as a new document.
  def upsert(iterable: Iterable[(String, Any)]): UpdateRequest = upsert(iterable.toMap)

  def upsert[T](t: T)(implicit indexable: Indexable[T]): UpdateRequest = upsert(indexable.json(t))
  def upsert(doc: String): UpdateRequest                               = copy(upsertSource = doc.some)

  def versionType(versionType: String): UpdateRequest = copy(versionType = versionType.some)
  def version(version: Long): UpdateRequest           = copy(version = version.some)
  def waitForActiveShards(waitForActiveShards: Int): UpdateRequest =
    copy(waitForActiveShards = waitForActiveShards.some)
}
