package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.queries.RankFeatureQuery._

case class RankFeatureQuery(field: String,
                            boost: Option[Double] = None,
                            saturation: Option[Saturation] = None,
                            log: Option[Log] = None,
                            sigmoid: Option[Sigmoid] = None,
                            linear: Option[Linear] = None) extends Query {

  def boost(boost: Double): RankFeatureQuery = copy(boost = boost.some)

  def withSaturation(saturation: Saturation): RankFeatureQuery =
    copy(saturation = saturation.some, log = None, sigmoid = None, linear = None)

  def withLog(log: Log): RankFeatureQuery =
    copy(log = log.some, saturation = None, sigmoid = None, linear = None)

  def withSigmoid(sigmoid: Sigmoid): RankFeatureQuery =
    copy(sigmoid = sigmoid.some, saturation = None, log = None, linear = None)

  def withLinear(linear: Linear): RankFeatureQuery =
    copy(linear = linear.some,  sigmoid = None, saturation = None, log = None)
}

object RankFeatureQuery {
  case class Saturation(pivot: Option[Float])
  case class Log(scalingFactor: Float)
  case class Sigmoid(pivot: Float, exponent: Double)
  case class Linear()
}
