package com.sksamuel.elastic4s

import scala.concurrent.duration._
import org.elasticsearch.action.bulk.{BulkRequestBuilder, BulkAction}
import com.sksamuel.elastic4s.mapping.MappingDsl

/** @author Stephen Samuel */
object ElasticDsl
  extends IndexDsl
  with CountDsl
  with CreateIndexDsl
  with DeleteIndexDsl
  with DeleteDsl
  with FacetDsl
  with GetDsl
  with MappingDsl
  with MoreLikeThisDsl
  with OptimizeDsl
  with PercolateDsl
  with SearchDsl
  with ScoreDsl
  with UpdateDsl
  with ValidateDsl {

  implicit val duration: Duration = 10.seconds

  class BulkDefinition(requests: Seq[BulkCompatibleDefinition]) extends RequestDefinition(BulkAction.INSTANCE) {
    def build = {
      val builder = new BulkRequestBuilder(null)
      requests.foreach(req => req match {
        case index: IndexDefinition => builder.add(index.build)
        case delete: DeleteByIdDefinition => builder.add(delete.build)
        case update: UpdateDefinition => builder.add(update.build)
      })
      builder.request
    }
  }

  object BulkDefinition {
    implicit def apply(requests: BulkCompatibleDefinition*): BulkDefinition = new BulkDefinition(requests)
  }

}
