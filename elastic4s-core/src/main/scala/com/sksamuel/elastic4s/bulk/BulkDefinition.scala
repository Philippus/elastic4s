package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.unit.TimeValue

import scala.concurrent.duration.Duration

case class BulkDefinition(requests: Seq[BulkCompatibleDefinition]) {

  def build = _builder

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(value: TimeValue): this.type = {
    _builder.timeout(value)
    this
  }

  def timeout(duration: Duration): this.type = {
    _builder.timeout(TimeValue.timeValueNanos(duration.toNanos))
    this
  }

  def refresh(refresh: RefreshPolicy): this.type = {
    _builder.setRefreshPolicy(refresh)
    this
  }

  private val _builder = new BulkRequest()
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
  }
}
