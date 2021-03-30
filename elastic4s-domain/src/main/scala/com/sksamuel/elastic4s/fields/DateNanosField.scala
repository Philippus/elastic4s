package com.sksamuel.elastic4s.fields

case class DateNanosField(name: String,
                          boost: Option[Double] = None,
                          copyTo: Seq[String] = Nil,
                          docValues: Option[Boolean] = None,
                          format: Option[String] = None,
                          locale: Option[String] = None,
                          ignoreMalformed: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          nullValue: Option[String] = None,
                          store: Option[Boolean] = None,
                          meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "date_nanos"
}
