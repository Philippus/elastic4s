package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{ DefinitionAttributeConsistencyLevel, DefinitionAttributeRefresh, DefinitionAttributeReplicationType, DefinitionAttributeTimeout }
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.bulk.BulkRequestBuilder

/** @author Stephen Samuel */
trait BulkCompatibleDefinition

trait BulkDsl {
  this: IndexDsl =>
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = new BulkDefinition(requests)
}

class BulkDefinition(requests: Seq[BulkCompatibleDefinition])
    extends DefinitionAttributeRefresh
    with DefinitionAttributeConsistencyLevel
    with DefinitionAttributeTimeout
    with DefinitionAttributeReplicationType {

  val _builder = new BulkRequestBuilder(null)
  requests.foreach {
    case index: IndexDefinition => _builder.add(index.build)
    case delete: DeleteByIdDefinition => _builder.add(delete.build)
    case update: UpdateDefinition => _builder.add(update.build)
  }
}