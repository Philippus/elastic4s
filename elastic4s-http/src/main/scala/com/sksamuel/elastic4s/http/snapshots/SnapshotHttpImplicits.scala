package com.sksamuel.elastic4s.http.snapshots

import cats.Functor
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.snapshots._
import org.apache.http.entity.ContentType

import scala.concurrent.duration._

case class CreateRepositoryResponse(acknowledged: Boolean)
case class CreateSnapshotResponse(accepted: Boolean)
case class GetSnapshotResponse(snapshots: Seq[Snapshot])
case class Snapshot(snapshot: String,
                    uuid: String,
                    @JsonProperty("version_id") versionId: String,
                    version: String,
                    indices: Seq[String],
                    state: String,
                    @JsonProperty("start_time") startTime: String,
                    @JsonProperty("start_time_in_millis") startTimeInMillis: Long,
                    @JsonProperty("end_time") endTime: String,
                    @JsonProperty("end_time_in_millis") endTimeInMillis: Long,
                    @JsonProperty("duration_in_millis") durationInMillis: Long) {
  def duration: Duration = durationInMillis.millis
}

case class DeleteSnapshotResponse(acknowledged: Boolean)
case class RestoreSnapshotResponse(acknowledged: Boolean)

trait SnapshotHttpImplicits {

  implicit object CreateRepositoryHttpExecutable extends HttpExecutable[CreateRepository, CreateRepositoryResponse] {

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: CreateRepository): F[HttpResponse] = {

      val endpoint = s"/_snapshot/" + request.name

      val params = scala.collection.mutable.Map.empty[String, String]
      request.verify.map(_.toString).foreach(params.put("verify", _))

      val body = XContentFactory.jsonBuilder()
      body.field("type", request.`type`)
      body.startObject("settings")
      request.settings.foreach { case (key, value) =>
        body.field(key, value.toString)
      }
      body.endObject()
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("PUT", endpoint, params.toMap, entity)
    }
  }

  implicit object CreateSnapshotHttpExecutable extends HttpExecutable[CreateSnapshot, CreateSnapshotResponse] {

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: CreateSnapshot): F[HttpResponse] = {

      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitForCompletion.map(_.toString).foreach(params.put("wait_for_completion", _))

      val body = XContentFactory.jsonBuilder()
      if (request.indices.isNonEmpty) {
        body.field("indices", request.indices.string)
      }
      request.ignoreUnavailable.foreach(body.field("ignore_unavailable", _))
      request.includeGlobalState.foreach(body.field("include_global_state", _))
      request.partial.foreach(body.field("partial", _))
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("PUT", endpoint, params.toMap, entity)
    }
  }

  implicit object DeleteSnapshotHttpExecutable extends HttpExecutable[DeleteSnapshot, DeleteSnapshotResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: DeleteSnapshot): F[HttpResponse] = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object GetSnapshotHttpExecutable extends HttpExecutable[GetSnapshots, GetSnapshotResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: GetSnapshots): F[HttpResponse] = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotNames.mkString(",")
      val params = scala.collection.mutable.Map.empty[String, String]
      request.ignoreUnavailable.map(_.toString).foreach(params.put("ignore_unavailable", _))
      request.verbose.map(_.toString).foreach(params.put("verbose", _))
      client.async("GET", endpoint, params.toMap)
    }
  }

  implicit object RestoreSnapshotDefinitionHttpExecutable extends HttpExecutable[RestoreSnapshot, RestoreSnapshotResponse] {
    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: RestoreSnapshot): F[HttpResponse] = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName + "/_restore"

      val body = XContentFactory.jsonBuilder()
      if (request.indices.isNonEmpty) {
        body.field("indices", request.indices.string)
      }
      request.ignoreUnavailable.foreach(body.field("ignore_unavailable", _))
      request.includeGlobalState.foreach(body.field("include_global_state", _))
      request.partial.foreach(body.field("partial", _))
      request.includeAliases.foreach(body.field("include_aliases", _))
      request.renamePattern.foreach(body.field("rename_pattern", _))
      request.renameReplacement.foreach(body.field("rename_replacement", _))
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("POST", endpoint, Map.empty, entity)
    }
  }
}
