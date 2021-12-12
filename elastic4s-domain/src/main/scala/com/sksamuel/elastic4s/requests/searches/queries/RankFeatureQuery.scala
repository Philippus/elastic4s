package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.queries.RankFeatureQuery._
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class RankFeatureQuery(field: String,
                            boost: Option[Double] = None,
                            saturation: Option[Saturation] = None,
                            log: Option[Log] = None,
                            sigmoid: Option[Sigmoid] = None) extends Query {

  def boost(boost: Double): RankFeatureQuery = copy(boost = boost.some)

  def withSaturation(saturation: Saturation): RankFeatureQuery =
    copy(saturation = saturation.some, log = None, sigmoid = None)

  def withLog(log: Log): RankFeatureQuery =
    copy(log = log.some, saturation = None, sigmoid = None)

  def withSigmoid(sigmoid: Sigmoid): RankFeatureQuery =
    copy(sigmoid = sigmoid.some, saturation = None, log = None)
}

object RankFeatureQuery {
  case class Saturation(pivot: Option[Int])
  case class Log(scalingFactor: Int)
  case class Sigmoid(pivot: Int, exponent: Double)
}
