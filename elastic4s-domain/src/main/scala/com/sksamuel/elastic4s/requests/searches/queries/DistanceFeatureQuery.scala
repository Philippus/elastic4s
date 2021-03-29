package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class DistanceFeatureQuery(field: String,
                                origin: String,
                                pivot: String,
                                boost: Option[Double] = None) extends Query {
  def boost(boost: Double): DistanceFeatureQuery = copy(boost = boost.some)
}
