package com.sksamuel.elastic4s.lock

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait LockDsl {

  def globalLock() = GlobalLockDefinition

  implicit object CreateIndexDefinitionExecutable
    extends Executable[GlobalLockDefinition.type, CreateIndexResponse, CreateIndexResponse] {
    override def apply(c: Client, t: GlobalLockDefinition.type): Future[CreateIndexResponse] = {
      null
    }
  }
}

object GlobalLockDefinition
