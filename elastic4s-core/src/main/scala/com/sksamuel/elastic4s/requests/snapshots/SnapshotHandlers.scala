package com.sksamuel.elastic4s.requests.snapshots

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, XContentFactory}

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

trait SnapshotHandlers {

  implicit object CreateRepositoryHandler extends Handler[CreateRepositoryRequest, CreateRepositoryResponse] {

    override def build(request: CreateRepositoryRequest): ElasticRequest = {

      val endpoint = s"/_snapshot/" + request.name

      val params = scala.collection.mutable.Map.empty[String, String]
      request.verify.map(_.toString).foreach(params.put("verify", _))

      val body = XContentFactory.jsonBuilder()
      body.field("type", request.`type`)
      body.startObject("settings")
      request.settings.foreach {
        case (key, value) =>
          body.field(key, value.toString)
      }
      body.endObject()
      val entity = HttpEntity(body.string, "application/json")

      ElasticRequest("PUT", endpoint, params.toMap, entity)
    }
  }

  implicit object CreateSnapshotHandler extends Handler[CreateSnapshotRequest, CreateSnapshotResponse] {

    override def build(request: CreateSnapshotRequest): ElasticRequest = {

      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitForCompletion.map(_.toString).foreach(params.put("wait_for_completion", _))

      val body = XContentFactory.jsonBuilder()
      if (request.indices.isNonEmpty)
        body.field("indices", request.indices.string)
      request.ignoreUnavailable.foreach(body.field("ignore_unavailable", _))
      request.includeGlobalState.foreach(body.field("include_global_state", _))
      request.partial.foreach(body.field("partial", _))
      val entity = HttpEntity(body.string, "application/json")

      ElasticRequest("PUT", endpoint, params.toMap, entity)
    }
  }

  implicit object DeleteSnapshotHandler extends Handler[DeleteSnapshotRequest, DeleteSnapshotResponse] {
    override def build(request: DeleteSnapshotRequest): ElasticRequest = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object GetSnapshotHandler extends Handler[GetSnapshotsRequest, GetSnapshotResponse] {
    override def build(request: GetSnapshotsRequest): ElasticRequest = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotNames.mkString(",")
      val params   = scala.collection.mutable.Map.empty[String, String]
      request.ignoreUnavailable.map(_.toString).foreach(params.put("ignore_unavailable", _))
      request.verbose.map(_.toString).foreach(params.put("verbose", _))
      ElasticRequest("GET", endpoint, params.toMap)
    }
  }

  implicit object RestoreSnapshotHandler extends Handler[RestoreSnapshotRequest, RestoreSnapshotResponse] {
    override def build(request: RestoreSnapshotRequest): ElasticRequest = {
      val endpoint = s"/_snapshot/" + request.repositoryName + "/" + request.snapshotName + "/_restore"

      val body = XContentFactory.jsonBuilder()
      if (request.indices.isNonEmpty)
        body.field("indices", request.indices.string)
      request.ignoreUnavailable.foreach(body.field("ignore_unavailable", _))
      request.includeGlobalState.foreach(body.field("include_global_state", _))
      request.partial.foreach(body.field("partial", _))
      request.includeAliases.foreach(body.field("include_aliases", _))
      request.renamePattern.foreach(body.field("rename_pattern", _))
      request.renameReplacement.foreach(body.field("rename_replacement", _))
      val entity = HttpEntity(body.string, "application/json")

      ElasticRequest("POST", endpoint, entity)
    }
  }
}
