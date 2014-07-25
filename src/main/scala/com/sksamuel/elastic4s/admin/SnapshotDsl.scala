package com.sksamuel.elastic4s
package admin

import org.elasticsearch.action.admin.cluster.repositories.put.{PutRepositoryRequestBuilder, PutRepositoryRequest}
import org.elasticsearch.action.admin.cluster.snapshots.create.{CreateSnapshotRequest, CreateSnapshotRequestBuilder}

/** @author Stephen Samuel
  *
  *         DSL Syntax:
  *
  *         repository create <name> settings <settings>
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


}

