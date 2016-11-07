package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{Executable, ProxyClients}
import org.elasticsearch.action.admin.cluster.repositories.put.{PutRepositoryRequest, PutRepositoryResponse}
import org.elasticsearch.action.admin.cluster.snapshots.create.{CreateSnapshotAction, CreateSnapshotRequestBuilder, CreateSnapshotResponse}
import org.elasticsearch.action.admin.cluster.snapshots.delete.{DeleteSnapshotAction, DeleteSnapshotRequestBuilder, DeleteSnapshotResponse}
import org.elasticsearch.action.admin.cluster.snapshots.get.{GetSnapshotsAction, GetSnapshotsRequestBuilder, GetSnapshotsResponse}
import org.elasticsearch.action.admin.cluster.snapshots.restore.{RestoreSnapshotAction, RestoreSnapshotRequestBuilder, RestoreSnapshotResponse}
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.client.Client

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait SnapshotDsl {

  def getSnapshot(names: String*): GetSnapshotExpectsFrom = getSnapshot(names)
  def getSnapshot(names: Iterable[String]): GetSnapshotExpectsFrom = new GetSnapshotExpectsFrom(names)

  class GetSnapshotExpectsFrom(names: Iterable[String]) {
    def from(repo: String) = new GetSnapshotsDefinition(names.toArray, repo)
  }

  def createSnapshot(name: String) = new CreateSnapshotExpectsIn(name)
  class CreateSnapshotExpectsIn(name: String) {
    def in(repo: String) = new CreateSnapshotDefinition(name, repo)
  }

  def deleteSnapshot(name: String) = new DeleteSnapshotExpectsIn(name)
  class DeleteSnapshotExpectsIn(name: String) {
    def in(repo: String) = new DeleteSnapshotDefinition(name, repo)
  }

  def restoreSnapshot(name: String) = new RestoreSnapshotExpectsFrom(name)
  class RestoreSnapshotExpectsFrom(name: String) {
    def from(repo: String) = RestoreSnapshotDefinition(name, repo)
  }

  def createRepository(name: String) = new CreateRepositoryExpectsType(name)
  class CreateRepositoryExpectsType(name: String) {
    def `type`(`type`: String) = new CreateRepositoryDefinition(name, `type`)
  }

  implicit object DeleteSnapshotDefinitionExecutable
    extends Executable[DeleteSnapshotDefinition, DeleteSnapshotResponse, DeleteSnapshotResponse] {
    override def apply(c: Client, t: DeleteSnapshotDefinition): Future[DeleteSnapshotResponse] = {
      injectFuture(c.admin.cluster.deleteSnapshot(t.build, _))
    }
  }

  implicit object RestoreSnapshotDefinitionExecutable
    extends Executable[RestoreSnapshotDefinition, RestoreSnapshotResponse, RestoreSnapshotResponse] {
    override def apply(c: Client, t: RestoreSnapshotDefinition): Future[RestoreSnapshotResponse] = {
      injectFuture(c.admin.cluster.restoreSnapshot(t.build, _))
    }
  }

  implicit object CreateSnapshotDefinitionExecutable
    extends Executable[CreateSnapshotDefinition, CreateSnapshotResponse, CreateSnapshotResponse] {
    override def apply(c: Client, t: CreateSnapshotDefinition): Future[CreateSnapshotResponse] = {
      injectFuture(c.admin.cluster.createSnapshot(t.build, _))
    }
  }

  implicit object GetSnapshotsDefinitionExecutable
    extends Executable[GetSnapshotsDefinition, GetSnapshotsResponse, GetSnapshotsResponse] {
    override def apply(c: Client, t: GetSnapshotsDefinition): Future[GetSnapshotsResponse] = {
      injectFuture(c.admin.cluster.getSnapshots(t.build, _))
    }
  }

  implicit object CreateRepositoryDefinitionExecutable
    extends Executable[CreateRepositoryDefinition, PutRepositoryResponse, PutRepositoryResponse] {
    override def apply(c: Client, t: CreateRepositoryDefinition): Future[PutRepositoryResponse] = {
      injectFuture(c.admin.cluster.putRepository(t.build, _))
    }
  }
}

class CreateRepositoryDefinition(name: String, `type`: String) {
  require(name.nonEmpty, "repository name must not be null or empty")
  require(`type`.nonEmpty, "repository name must not be null or empty")

  protected val request = new PutRepositoryRequest(name).`type`(`type`)
  def build = request
  def settings(map: Map[String, AnyRef]): this.type = {
    request.settings(map.asJava)
    this
  }
}

class DeleteSnapshotDefinition(name: String, repo: String) {
  val request = new DeleteSnapshotRequestBuilder(ProxyClients.cluster, DeleteSnapshotAction.INSTANCE, repo, name)
  def build = request.request()
}

class GetSnapshotsDefinition(snapshotNames: Array[String], repo: String) {
  val request = new GetSnapshotsRequestBuilder(ProxyClients.cluster, GetSnapshotsAction.INSTANCE, repo)
    .setSnapshots(snapshotNames: _*)
  def build = request.request()
}

class CreateSnapshotDefinition(name: String, repo: String) {
  require(name.nonEmpty, "snapshot name must not be null or empty")
  require(repo.nonEmpty, "repo name must not be null or empty")

  val request = new CreateSnapshotRequestBuilder(ProxyClients.cluster, CreateSnapshotAction.INSTANCE, repo, name)
  def build = request.request()

  def partial(p: Boolean): this.type = {
    request.setPartial(p)
    this
  }

  def setIndicesOptions(indicesOptions: IndicesOptions): this.type = {
    request.setIndicesOptions(indicesOptions)
    this
  }

  def includeGlobalState(global: Boolean): this.type = {
    request.setIncludeGlobalState(global)
    this
  }

  def waitForCompletion(waitForCompletion: Boolean): this.type = {
    request.setWaitForCompletion(waitForCompletion)
    this
  }

  def index(index: String): this.type = {
    request.setIndices(index)
    this
  }

  def indexes(indexes: String*): this.type = {
    request.setIndices(indexes: _*)
    this
  }

  def settings(map: Map[String, AnyRef]): this.type = {
    request.setSettings(map.asJava)
    this
  }
}

case class RestoreSnapshotDefinition(name: String, repo: String) {
  require(name.nonEmpty, "snapshot name must not be null or empty")
  require(repo.nonEmpty, "repo must not be null or empty")

  val request = new RestoreSnapshotRequestBuilder(ProxyClients.cluster, RestoreSnapshotAction.INSTANCE, repo, name)
  def build = request.request()

  def restoreGlobalState(global: Boolean): this.type = {
    request.setRestoreGlobalState(global)
    this
  }

  def renamePattern(renamePattern: String): this.type = {
    request.setRenamePattern(renamePattern)
    this
  }

  def renameReplacement(renameReplacement: String): this.type = {
    request.setRenameReplacement(renameReplacement)
    this
  }

  def partial(partial: Boolean): this.type = {
    request.setPartial(partial)
    this
  }

  def includeAliases(includeAliases: Boolean): this.type = {
    request.setIncludeAliases(includeAliases)
    this
  }

  def ignoreIndexSettings(ignoreIndexSettings: String*): this.type = {
    request.setIgnoreIndexSettings(ignoreIndexSettings: _*)
    this
  }

  def waitForCompletion(waitForCompletion: Boolean): this.type = {
    request.setWaitForCompletion(waitForCompletion)
    this
  }

  def index(index: String): this.type = {
    request.setIndices(index)
    this
  }

  def indexes(indexes: String*): this.type = {
    request.setIndices(indexes: _*)
    this
  }

  def settings(map: Map[String, AnyRef]): this.type = {
    import scala.collection.JavaConverters._
    request.setSettings(map.asJava)
    this
  }
}
