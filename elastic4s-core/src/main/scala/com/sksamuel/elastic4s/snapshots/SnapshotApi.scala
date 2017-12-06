package com.sksamuel.elastic4s.snapshots

trait SnapshotApi {

  def getSnapshot(snapshotName: String, repository: String) = getSnapshots(Seq(snapshotName), repository)
  def getSnapshots(snapshotNames: Seq[String], repository: String) = GetSnapshotsDefinition(snapshotNames, repository)

  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: String*): GetSnapshotExpectsFrom = getSnapshot(names)
  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: Iterable[String]): GetSnapshotExpectsFrom = new GetSnapshotExpectsFrom(names)
  class GetSnapshotExpectsFrom(names: Iterable[String]) {
    @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) =  GetSnapshotsDefinition(names.toSeq, repo)
  }

  def createSnapshot(snapshotName: String, repository: String) = CreateSnapshotDefinition(snapshotName, repository)

  @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
  def createSnapshot(name: String) = new CreateSnapshotExpectsIn(name)
  class CreateSnapshotExpectsIn(name: String) {
    @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = CreateSnapshotDefinition(name, repo)
  }

  def deleteSnapshot(snapshotName: String, repository: String) = DeleteSnapshotDefinition(snapshotName, repository)

  @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
  def deleteSnapshot(name: String) = new DeleteSnapshotExpectsIn(name)
  class DeleteSnapshotExpectsIn(name: String) {
    @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = DeleteSnapshotDefinition(name, repo)
  }

  def restoreSnapshot(snapshotName: String, repository: String) = RestoreSnapshotDefinition(snapshotName, repository)

  @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
  def restoreSnapshot(name: String) = new RestoreSnapshotExpectsFrom(name)
  class RestoreSnapshotExpectsFrom(name: String) {
    @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) = RestoreSnapshotDefinition(name, repo)
  }

  def createRepository(snapshotName: String, `type`: String) = CreateRepositoryDefinition(snapshotName, `type`)

  @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
  def createRepository(name: String) = new CreateRepositoryExpectsType(name)
  class CreateRepositoryExpectsType(name: String) {
    @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
    def `type`(`type`: String) = CreateRepositoryDefinition(name, `type`)
  }
}
