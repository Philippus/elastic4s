package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.common.unit.TimeValue

/** @author Stephen Samuel */
trait BulkCompatibleDefinition

trait BulkDsl {
  this: IndexDsl =>
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = new BulkDefinition(requests)
}

class BulkDefinition(requests: Seq[BulkCompatibleDefinition]) {

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(value: TimeValue): this.type = {
    _builder.timeout(value)
    this
  }

  def replicationType(replicationType: ReplicationType): this.type = {
    _builder.replicationType(replicationType)
    this
  }

  def refresh(refresh: Boolean): this.type = {
    _builder.refresh(refresh)
    this
  }

  def consistencyLevel(level: WriteConsistencyLevel): this.type = {
    _builder.consistencyLevel(level)
    this
  }

  val _builder = new BulkRequest()
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
    case register: RegisterDefinition => _builder.add(register.build)
  }
}