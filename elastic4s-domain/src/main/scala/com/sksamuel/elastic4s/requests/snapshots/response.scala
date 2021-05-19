package com.sksamuel.elastic4s.requests.snapshots

import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo}
import com.sksamuel.elastic4s.requests.common.Shards
import scala.concurrent.duration._

case class CreateRepositoryResponse(acknowledged: Boolean)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "accepted")
@JsonSubTypes(
  Array(
    new JsonSubTypes.Type(value = classOf[CreateSnapshotResponseAsync], name = "accepted"),
    new JsonSubTypes.Type(value = classOf[CreateSnapshotResponseAwait], name = "snapshot")))
sealed trait CreateSnapshotResponse { def succeeded: Boolean }
case class CreateSnapshotResponseAsync(accepted: Boolean) extends CreateSnapshotResponse {
  override def succeeded: Boolean = accepted
}
case class CreateSnapshotResponseAwait(snapshot: Snapshot) extends CreateSnapshotResponse {
  override def succeeded: Boolean = snapshot.state == "SUCCESS"
}
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
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "accepted")
@JsonSubTypes(
  Array(
    new JsonSubTypes.Type(value = classOf[RestoreSnapshotResponseAsync], name = "accepted"),
    new JsonSubTypes.Type(value = classOf[RestoreSnapshotResponseAwait], name = "snapshot")))
sealed trait RestoreSnapshotResponse {
  def succeeded: Boolean
}
case class RestoreSnapshotResponseAsync(accepted: Boolean) extends RestoreSnapshotResponse {
  override def succeeded: Boolean = accepted
}
case class RestoreSnapshotResponseSnapshot(snapshot: String, indices: Seq[String], shards: Shards)
case class RestoreSnapshotResponseAwait(snapshot: RestoreSnapshotResponseSnapshot) extends RestoreSnapshotResponse {
  override def succeeded: Boolean = snapshot.shards.failed == 0
}
