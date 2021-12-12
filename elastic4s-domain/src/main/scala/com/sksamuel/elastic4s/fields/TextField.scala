package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits
import com.sksamuel.elastic4s.requests.mappings.FielddataFrequencyFilter

case class TextField(override val name: String,
                     analyzer: Option[String] = None,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     fields: List[ElasticField] = Nil,
                     fielddata: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/fielddata.html
                     fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
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
  def analyzer(name: String): TextField = copy(analyzer = Option(name))
  def searchAnalyzer(name: String): TextField = copy(searchAnalyzer = Option(name))
  def searchQuoteAnalyzer(name: String): TextField = copy(searchQuoteAnalyzer = Option(name))
  def copyTo(copyTo: String*): TextField = copy(copyTo = copyTo.toList)
  def copyTo(copyTo: Iterable[String]): TextField = copy(copyTo = copyTo.toList)
  def fielddata(fielddata: Boolean): TextField = copy(fielddata = fielddata.some)
  def fields(fields: ElasticField*): TextField = copy(fields = fields.toList)
  def fields(fields: Iterable[ElasticField]): TextField = copy(fields = fields.toList)
  def stored(store: Boolean): TextField = copy(store = store.some)
  def index(index: Boolean): TextField = copy(index = index.some)
  def indexOptions(indexOptions: String): TextField = copy(indexOptions = indexOptions.some)
  def norms(norms: Boolean): TextField = copy(norms = norms.some)
  def termVector(termVector: String): TextField = copy(termVector = termVector.some)
  def store(store: Boolean): TextField = copy(store = store.some)
  def similarity(similarity: String): TextField = copy(similarity = similarity.some)
  def boost(boost: Double): TextField = copy(boost = boost.some)
}

case class IndexPrefixes(minChars: Int, maxChars: Int)

case class MatchOnlyTextField(name: String, fields: List[ElasticField] = Nil) extends ElasticField {
  override def `type`: String = "match_only_text"
}
