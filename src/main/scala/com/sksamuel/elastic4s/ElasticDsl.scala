package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin.SnapshotDsl

import scala.concurrent.duration._
import com.sksamuel.elastic4s.mappings.MappingDsl

/** @author Stephen Samuel */
trait ElasticDsl
    extends IndexDsl
    with AliasesDsl
    with BulkDsl
    with ClusterDsl
    with CountDsl
    with CreateIndexDsl
    with DeleteIndexDsl
    with DeleteDsl
    with ExplainDsl
    with FacetDsl
    with AggregationDsl
    with GetDsl
    with IndexStatusDsl
    with MappingDsl
    with MoreLikeThisDsl
    with MultiGetDsl
    with OptimizeDsl
    with PercolateDsl
    with PutMappingDsl
    with SearchDsl
    with ScoreDsl
    with SnapshotDsl
    with UpdateDsl
    with ValidateDsl {

  implicit val duration: Duration = 10.seconds

}

object ElasticDsl extends ElasticDsl
