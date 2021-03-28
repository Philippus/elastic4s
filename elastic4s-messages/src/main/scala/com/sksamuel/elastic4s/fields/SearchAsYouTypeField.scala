package com.sksamuel.elastic4s.fields

case class SearchAsYouTypeField(name: String,
                                analyzer: Option[String] = None,
                                boost: Option[Double] = None,
                                copyTo: Seq[String] = Nil,
                                docValues: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/doc-values.html
                                fielddata: Option[Boolean] = None,
                                ignoreAbove: Option[Int] = None,
                                index: Option[Boolean] = None,
                                indexOptions: Option[String] = None,
                                maxShingleSize: Option[Int] = None,
                                norms: Option[Boolean] = None,
                                similarity: Option[String] = None,
                                store: Option[Boolean] = None,
                                termVector: Option[String] = None,
                                meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "search_as_you_type"
}
