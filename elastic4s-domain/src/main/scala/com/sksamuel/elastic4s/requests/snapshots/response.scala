package com.sksamuel.elastic4s.requests.snapshots

import com.fasterxml.jackson.annotation.JsonProperty

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


