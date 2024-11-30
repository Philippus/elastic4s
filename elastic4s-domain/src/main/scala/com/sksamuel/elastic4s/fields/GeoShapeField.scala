package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits._

object GeoShapeField {
  val `type`: String = "geo_shape"
}
case class GeoShapeField(
    name: String,
    boost: Option[Double] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    ignoreZValue: Option[Boolean] = None,
    index: Option[Boolean] = None,
    norms: Option[Boolean] = None,
    nullValue: Option[String] = None,
    store: Option[Boolean] = None,
    tree: Option[String] = None,
    precision: Option[String] = None,
    strategy: Option[String] = None,
    distanceErrorPct: Option[Double] = None,
    orientation: Option[String] = None,
    pointsOnly: Option[Boolean] = None,
    treeLevels: Option[String] = None,
    meta: Map[String, Any] = Map.empty
) extends ElasticField {
  override def `type`: String = GeoShapeField.`type`

  def tree(tree: String): GeoShapeField           = copy(tree = tree.some)
  def precision(precision: String): GeoShapeField = copy(precision = precision.some)
  def norms(norms: Boolean): GeoShapeField        = copy(norms = norms.some)
}
