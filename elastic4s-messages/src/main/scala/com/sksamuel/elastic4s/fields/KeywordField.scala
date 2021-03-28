package com.sksamuel.elastic4s.fields

case class KeywordField(name: String,
                        boost: Option[Double] = None,
                        copyTo: Seq[String] = Nil,
                        docValues: Option[Boolean] = None,
                        eagerGlobalOrdinals: Option[Boolean] = None,
                        fields: List[ElasticField] = Nil,
                        ignoreAbove: Option[Int] = None,
                        index: Option[Boolean] = None,
                        indexOptions: Option[String] = None,
                        norms: Option[Boolean] = None,
                        normalizer: Option[String] = None,
                        nullValue: Option[String] = None,
                        similarity: Option[String] = None,
                        splitQueriesOnWhitespace: Option[Boolean] = None,
                        store: Option[Boolean] = None,
                        termVector: Option[String] = None,
                        meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "keyword"
}
