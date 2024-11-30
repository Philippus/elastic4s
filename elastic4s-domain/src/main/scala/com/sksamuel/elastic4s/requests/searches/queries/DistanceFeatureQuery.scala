package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class DistanceFeatureQuery(field: String, origin: String, pivot: String, boost: Option[Double] = None)
    extends Query {
  def boost(boost: Double): DistanceFeatureQuery = copy(boost = boost.some)
}
