package com.sksamuel.elastic4s.repository

trait SnapshotApi {

  def getSnapshot(name: String, repository: String) = getSnapshots(Seq(name), repository)
  def getSnapshots(name: Seq[String], repository: String) = GetSnapshotsDefinition(name, repository)

  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: String*): GetSnapshotExpectsFrom = getSnapshot(names)
  @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
  def getSnapshot(names: Iterable[String]): GetSnapshotExpectsFrom = new GetSnapshotExpectsFrom(names)
  class GetSnapshotExpectsFrom(names: Iterable[String]) {
    @deprecated("use getSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) =  GetSnapshotsDefinition(names.toArray, repo)
  }

  def createSnapshot(name: String, repository: String) = CreateSnapshotDefinition(name, repository)

  @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
  def createSnapshot(name: String) = new CreateSnapshotExpectsIn(name)
  class CreateSnapshotExpectsIn(name: String) {
    @deprecated("use createSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = CreateSnapshotDefinition(name, repo)
  }

  def deleteSnapshot(name: String, repository: String) = DeleteSnapshotDefinition(name, repository)

  @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
  def deleteSnapshot(name: String) = new DeleteSnapshotExpectsIn(name)
  class DeleteSnapshotExpectsIn(name: String) {
    @deprecated("use deleteSnapshot(name: String, repository: String)", "6.0.2")
    def in(repo: String) = DeleteSnapshotDefinition(name, repo)
  }

  def restoreSnapshot(name: String, repository: String) = RestoreSnapshotDefinition(name, repository)

  @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
  def restoreSnapshot(name: String) = new RestoreSnapshotExpectsFrom(name)
  class RestoreSnapshotExpectsFrom(name: String) {
    @deprecated("use restoreSnapshot(name: String, repository: String)", "6.0.2")
    def from(repo: String) = RestoreSnapshotDefinition(name, repo)
  }

  def createRepository(name: String, repository: String) = CreateRepositoryDefinition(name, repository)

  @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
  def createRepository(name: String) = new CreateRepositoryExpectsType(name)
  class CreateRepositoryExpectsType(name: String) {
    @deprecated("use createRepository(name: String, repository: String)", "6.0.2")
    def `type`(`type`: String) = CreateRepositoryDefinition(name, `type`)
  }
}
