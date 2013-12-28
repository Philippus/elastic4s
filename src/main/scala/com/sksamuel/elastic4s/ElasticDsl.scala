package com.sksamuel.elastic4s

import scala.concurrent.duration._
import com.sksamuel.elastic4s.mapping.MappingDsl

/** @author Stephen Samuel */
object ElasticDsl
  extends IndexDsl
  with CountDsl
  with CreateIndexDsl
  with DeleteIndexDsl
  with DeleteDsl
  with ExplainDsl
  with FacetDsl
  with GetDsl
  with MappingDsl
  with MoreLikeThisDsl
  with MultiGetDsl
  with OptimizeDsl
  with PercolateDsl
  with SearchDsl
  with ScoreDsl
  with UpdateDsl
  with ValidateDsl {

  implicit val duration: Duration = 10.seconds

}
