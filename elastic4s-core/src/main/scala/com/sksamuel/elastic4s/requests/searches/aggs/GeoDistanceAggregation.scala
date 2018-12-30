package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.geo.GeoDistance
import com.sksamuel.exts.OptionImplicits._

case class GeoDistanceAggregation(name: String,
                                  origin: GeoPoint,
                                  field: Option[String] = None,
                                  format: Option[String] = None,
                                  missing: Option[AnyRef] = None,
                                  keyed: Option[Boolean] = None,
                                  distanceType: Option[GeoDistance] = None,
                                  unit: Option[DistanceUnit] = None,
                                  ranges: Seq[(Option[String], Double, Double)] = Nil,
                                  unboundedFrom: Option[(Option[String], Double)] = None,
                                  unboundedTo: Option[(Option[String], Double)] = None,
                                  script: Option[Script] = None,
                                  subaggs: Seq[AbstractAggregation] = Nil,
                                  metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = GeoDistanceAggregation

  def ranges(ranges: (Double, Double)*): T = copy(ranges = ranges.map { case (from, to) => (None, from, to) })

  def range(from: Double, to: Double): T              = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: Double, to: Double): T = copy(ranges = ranges :+ (Some(key), from, to))

  def unboundedFrom(from: Double): T              = copy(unboundedFrom = (None, from).some)
  def unboundedFrom(key: String, from: Double): T = copy(unboundedFrom = (Some(key), from).some)

  def unboundedTo(to: Double): T              = copy(unboundedTo = (None, to).some)
  def unboundedTo(key: String, to: Double): T = copy(unboundedTo = (Some(key), to).some)

  def distanceType(distanceType: GeoDistance): T = copy(distanceType = distanceType.some)
  def geoDistance(geoDistance: GeoDistance): T   = copy(distanceType = geoDistance.some)
  def unit(unit: DistanceUnit): T                = copy(unit = unit.some)

  def keyed(keyed: Boolean): T    = copy(keyed = keyed.some)
  def field(field: String): T     = copy(field = field.some)
  def format(format: String): T   = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: Script): T   = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
