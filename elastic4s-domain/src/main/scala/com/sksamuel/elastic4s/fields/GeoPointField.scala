package com.sksamuel.elastic4s.fields

case class GeoPointField(name: String,
                         boost: Option[Double] = None,
                         copyTo: Seq[String] = Nil,
                         docValues: Option[Boolean] = None,
                         ignoreMalformed: Option[Boolean] = None,
                         ignoreZValue: Option[Boolean] = None,
                         index: Option[Boolean] = None,
                         norms: Option[Boolean] = None,
                         nullValue: Option[String] = None,
                         store: Option[Boolean] = None,
                         meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "geo_point"
}
