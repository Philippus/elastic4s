package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.requests.mappings.FielddataFrequencyFilter

case class TextField(override val name: String,
                     analyzer: Option[String] = None,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     fields: List[ElasticField] = Nil,
                     fielddata: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/fielddata.html
                     fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                     ignoreAbove: Option[Int] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/ignore-above.html
                     index: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-index.html
                     indexPrefixes: Option[IndexPrefixes] = None,
                     indexPhrases: Option[Boolean] = None,
                     indexOptions: Option[String] = None,
                     norms: Option[Boolean] = None,
                     positionIncrementGap: Option[Int] = None,
                     searchAnalyzer: Option[String] = None,
                     searchQuoteAnalyzer: Option[String] = None,
                     similarity: Option[String] = None,
                     store: Option[Boolean] = None,
                     termVector: Option[String] = None,
                     meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "text"
  def analyzer(name:String): TextField = copy(analyzer = Option(name))
}

case class IndexPrefixes(minChars: Int, maxChars: Int)
