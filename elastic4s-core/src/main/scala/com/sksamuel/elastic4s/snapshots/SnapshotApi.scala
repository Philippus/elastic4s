package com.sksamuel.elastic4s.snapshots

trait SnapshotApi {

  def getSnapshot(snapshotName: String, repository: String): GetSnapshots = getSnapshots(Seq(snapshotName), repository)
  def getSnapshots(snapshotNames: Seq[String], repository: String) = GetSnapshots(snapshotNames, repository)

  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: String*): GetSnapshotExpectsFrom = getSnapshot(names)
  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: Iterable[String]): GetSnapshotExpectsFrom = new GetSnapshotExpectsFrom(names)
  class GetSnapshotExpectsFrom(names: Iterable[String]) {
    @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) =  GetSnapshots(names.toSeq, repo)
  }

  def createSnapshot(snapshotName: String, repository: String) = CreateSnapshot(snapshotName, repository)

  @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
  def createSnapshot(name: String) = new CreateSnapshotExpectsIn(name)
  class CreateSnapshotExpectsIn(name: String) {
    @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = CreateSnapshot(name, repo)
  }

  def deleteSnapshot(snapshotName: String, repository: String) = DeleteSnapshot(snapshotName, repository)

  @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
  def deleteSnapshot(name: String) = new DeleteSnapshotExpectsIn(name)
  class DeleteSnapshotExpectsIn(name: String) {
    @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = DeleteSnapshot(name, repo)
  }

  def restoreSnapshot(snapshotName: String, repository: String) = RestoreSnapshot(snapshotName, repository)

  @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
  def restoreSnapshot(name: String) = new RestoreSnapshotExpectsFrom(name)
  class RestoreSnapshotExpectsFrom(name: String) {
    @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) = RestoreSnapshot(name, repo)
  }

  def createRepository(snapshotName: String, `type`: String) = CreateRepository(snapshotName, `type`)

  @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
  def createRepository(name: String) = new CreateRepositoryExpectsType(name)
  class CreateRepositoryExpectsType(name: String) {
    @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
    def `type`(`type`: String) = CreateRepository(name, `type`)
  }
}
