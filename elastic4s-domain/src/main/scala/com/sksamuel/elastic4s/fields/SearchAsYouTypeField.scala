package com.sksamuel.elastic4s.fields

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class SearchAsYouTypeField(name: String,
                                analyzer: Option[String] = None,
                                searchAnalyzer: Option[String] = None,
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

  def analyzer(name: String): SearchAsYouTypeField = copy(analyzer = Option(name))
  def searchAnalyzer(name: String): SearchAsYouTypeField = copy(searchAnalyzer = Option(name))
  def copyTo(copyTo: String*): SearchAsYouTypeField = copy(copyTo = copyTo.toList)
  def copyTo(copyTo: Iterable[String]): SearchAsYouTypeField = copy(copyTo = copyTo.toList)
  def fielddata(fielddata: Boolean): SearchAsYouTypeField = copy(fielddata = fielddata.some)
  def stored(store: Boolean): SearchAsYouTypeField = copy(store = store.some)
  def index(index: Boolean): SearchAsYouTypeField = copy(index = index.some)
  def indexOptions(indexOptions: String): SearchAsYouTypeField = copy(indexOptions = indexOptions.some)
  def norms(norms: Boolean): SearchAsYouTypeField = copy(norms = norms.some)
  def termVector(termVector: String): SearchAsYouTypeField = copy(termVector = termVector.some)
  def similarity(similarity: String): SearchAsYouTypeField = copy(similarity = similarity.some)
  def boost(boost: Double): SearchAsYouTypeField = copy(boost = boost.some)
  def ignoreAbove(ignoreAbove: Int): SearchAsYouTypeField = copy(ignoreAbove = ignoreAbove.some)
  def maxShingleSize(maxShingleSize: Int): SearchAsYouTypeField = copy(maxShingleSize = maxShingleSize.some)
}
