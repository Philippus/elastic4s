package com.sksamuel.elastic4s
package admin

import org.elasticsearch.action.admin.cluster.repositories.put.{PutRepositoryRequestBuilder, PutRepositoryRequest}
import org.elasticsearch.action.admin.cluster.snapshots.create.{CreateSnapshotRequest, CreateSnapshotRequestBuilder}

/** @author Stephen Samuel
  *
  *         DSL Syntax:
  *
  *         repository create <repo> settings <settings>
  *         snapshot create <name> in <repo>
  *         snapshot delete <name> in <repo>
  *         snapshot restore <name> from <repo>
  *
  **/
trait SnapshotDsl {

  def repository = RepositoryPrefix

  object RepositoryPrefix {
    def create(name: String) = new CreateRepositoryDefinition(name)
  }

  class CreateRepositoryDefinition(name: String) {
    val request = new PutRepositoryRequestBuilder(null, name)
    def build = request.request()
    def `type`(_type: String): this.type = {
      request.setType(_type)
      this
    }
    def settings(map: Map[String, AnyRef]): this.type = {
      import scala.collection.JavaConverters._
      request.setSettings(map.asJava)
      this
    }
  }

  def snapshot = SnapshotPrefix

  object SnapshotPrefix {
    def create(name: String) = new CreateSnapshotExpectsIn(name)
  }

  class CreateSnapshotExpectsIn(name: String) {
    def in(repo: String) = new CreateSnapshotDefinition(name, repo)
  }

  class CreateSnapshotDefinition(name: String, repo: String) {
    val request = new CreateSnapshotRequestBuilder(null, repo, name)
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
      import scala.collection.JavaConverters._
      request.setSettings(map.asJava)
      this
    }
  }
}

