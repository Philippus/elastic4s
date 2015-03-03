package com.sksamuel.elastic4s
package admin

import org.elasticsearch.action.admin.cluster.repositories.put.{ PutRepositoryResponse, PutRepositoryRequestBuilder }
import org.elasticsearch.action.admin.cluster.snapshots.create.{ CreateSnapshotRequestBuilder, CreateSnapshotResponse }
import org.elasticsearch.action.admin.cluster.snapshots.delete.{ DeleteSnapshotRequestBuilder, DeleteSnapshotResponse }
import org.elasticsearch.action.admin.cluster.snapshots.restore.{ RestoreSnapshotRequestBuilder, RestoreSnapshotResponse }
import org.elasticsearch.client.Client

import scala.collection.JavaConverters._
import scala.concurrent.Future

/** @author Stephen Samuel
  *
  *     DSL Syntax:
  *
  *     repository create <repo> settings <settings>
  *     snapshot create <name> in <repo>
  *     snapshot delete <name> in <repo>
  *     snapshot restore <name> from <repo>
  *
  */
trait SnapshotDsl {

  class CreateRepositoryExpectsType(name: String) {
    def `type`(`type`: String) = new CreateRepositoryDefinition(name, `type`)
  }

  class CreateSnapshotExpectsIn(name: String) {
    def in(repo: String) = new CreateSnapshotDefinition(name, repo)
  }

  class RestoreSnapshotExpectsFrom(name: String) {
    def from(repo: String) = new RestoreSnapshotDefinition(name, repo)
  }

  class DeleteSnapshotExpectsIn(name: String) {
    def in(repo: String) = new DeleteSnapshotDefinition(name, repo)
  }

  implicit object DeleteSnapshotDefinitionExecutable
      extends Executable[DeleteSnapshotDefinition, DeleteSnapshotResponse] {
    override def apply(c: Client, t: DeleteSnapshotDefinition): Future[DeleteSnapshotResponse] = {
      injectFuture(c.admin.cluster.deleteSnapshot(t.build, _))
    }
  }

  implicit object RestoreSnapshotDefinitionExecutable
      extends Executable[RestoreSnapshotDefinition, RestoreSnapshotResponse] {
    override def apply(c: Client, t: RestoreSnapshotDefinition): Future[RestoreSnapshotResponse] = {
      injectFuture(c.admin.cluster.restoreSnapshot(t.build, _))
    }
  }

  implicit object CreateSnapshotDefinitionExecutable
      extends Executable[CreateSnapshotDefinition, CreateSnapshotResponse] {
    override def apply(c: Client, t: CreateSnapshotDefinition): Future[CreateSnapshotResponse] = {
      injectFuture(c.admin.cluster.createSnapshot(t.build, _))
    }
  }

  implicit object CreateRepositoryDefinitionExecutable
      extends Executable[CreateRepositoryDefinition, PutRepositoryResponse] {
    override def apply(c: Client, t: CreateRepositoryDefinition): Future[PutRepositoryResponse] = {
      injectFuture(c.admin.cluster.putRepository(t.build, _))
    }
  }
}

class CreateRepositoryDefinition(name: String, `type`: String) {
  val request = new PutRepositoryRequestBuilder(ProxyClients.cluster, name).setType(`type`)
  def build = request.request()
  def settings(map: Map[String, AnyRef]): this.type = {
    request.setSettings(map.asJava)
    this
  }
}

class DeleteSnapshotDefinition(name: String, repo: String) {
  val request = new DeleteSnapshotRequestBuilder(ProxyClients.cluster, repo, name)
  def build = request.request()
}

class CreateSnapshotDefinition(name: String, repo: String) {
  val request = new CreateSnapshotRequestBuilder(ProxyClients.cluster, repo, name)
  def build = request.request()

  def partial(p: Boolean): this.type = {
    request.setPartial(p)
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

class RestoreSnapshotDefinition(name: String, repo: String) {
  val request = new RestoreSnapshotRequestBuilder(ProxyClients.cluster, repo, name)
  def build = request.request()

  def restoreGlobalState(global: Boolean): this.type = {
    request.setRestoreGlobalState(global)
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
