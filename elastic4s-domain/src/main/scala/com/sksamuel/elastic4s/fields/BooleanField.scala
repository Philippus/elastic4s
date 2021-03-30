package com.sksamuel.elastic4s.fields

case class BooleanField(name: String,
                        boost: Option[Double] = None,
                        copyTo: Seq[String] = Nil, // https://www.elastic.co/guide/en/elasticsearch/reference/current/copy-to.html
                        docValues: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        nullValue: Option[Boolean] = None,
                        store: Option[Boolean] = None,
                        meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "boolean"
}
