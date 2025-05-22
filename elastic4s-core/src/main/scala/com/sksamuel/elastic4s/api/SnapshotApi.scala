package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.snapshots.{
  CreateRepositoryRequest,
  CreateSnapshotRequest,
  DeleteRepositoryRequest,
  DeleteSnapshotRequest,
  GetRepositoryRequest,
  GetSnapshotsRequest,
  RestoreSnapshotRequest
}

trait SnapshotApi {
  def getSnapshot(snapshotName: String, repository: String): GetSnapshotsRequest =
    getSnapshots(Seq(snapshotName), repository)
  def getSnapshots(snapshotNames: Seq[String], repository: String)               = GetSnapshotsRequest(snapshotNames, repository)

  def createSnapshot(snapshotName: String, repository: String): CreateSnapshotRequest =
    CreateSnapshotRequest(snapshotName, repository)

  def deleteSnapshot(snapshotName: String, repository: String): DeleteSnapshotRequest =
    DeleteSnapshotRequest(snapshotName, repository)

  def restoreSnapshot(snapshotName: String, repository: String): RestoreSnapshotRequest =
    RestoreSnapshotRequest(snapshotName, repository)

  def createRepository(repositoryName: String, `type`: String): CreateRepositoryRequest =
    CreateRepositoryRequest(repositoryName, `type`)

  def getRepository(repositoryName: String): GetRepositoryRequest = GetRepositoryRequest(repositoryName)

  def deleteRepository(repositoryName: String): DeleteRepositoryRequest = DeleteRepositoryRequest(repositoryName)
}
