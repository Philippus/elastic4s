package com.sksamuel.elastic4s

import scala.concurrent.duration._

/** @author Stephen Samuel */
object ElasticDsl
  extends IndexDsl
  with CountDsl
  with CreateIndexDsl
  with DeleteDsl
  with GetDsl
  with MoreLikeThisDsl
  with PercolateDsl
  with SearchDsl
  with UpdateDsl
  with ValidateDsl {
    implicit val duration: Duration = 10.seconds

}
