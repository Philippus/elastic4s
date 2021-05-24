package com.sksamuel.elastic4s.fields

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class DynamicField(override val name: String,
                        analyzer: Option[String] = None,
                        boost: Option[Double] = None,
                        coerce: Option[Boolean] = None,
                        copyTo: Seq[String] = Nil,
                        docValues: Option[Boolean] = None,
                        enabled: Option[Boolean] = None,
                        fielddata: Option[Boolean] = None,
                        fields: List[ElasticField] = Nil,
                        format: Option[String] = None,
                        ignoreAbove: Option[Int] = None,
                        ignoreMalformed: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        indexOptions: Option[String] = None,
                        locale: Option[String] = None,
                        norms: Option[Boolean] = None,
                        nullValue: Option[String] = None,
                        scalingFactor: Option[Double] = None,
                        similarity: Option[String] = None,
                        store: Option[Boolean] = None,
                        termVector: Option[String] = None,
               meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "{dynamic_type}"
  def analyzer(name: String): DynamicField = copy(analyzer = Option(name))
  def boost(boost: Double): DynamicField = copy(boost = boost.some)
  def copyTo(copyTo: String*): DynamicField = copy(copyTo = copyTo.toList)
  def copyTo(copyTo: Iterable[String]): DynamicField = copy(copyTo = copyTo.toList)
  def fielddata(fielddata: Boolean): DynamicField = copy(fielddata = fielddata.some)
  def fields(fields: ElasticField*): DynamicField = copy(fields = fields.toList)
  def fields(fields: Iterable[ElasticField]): DynamicField = copy(fields = fields.toList)
  def stored(store: Boolean): DynamicField = copy(store = store.some)
  def index(index: Boolean): DynamicField = copy(index = index.some)
  def indexOptions(indexOptions: String): DynamicField = copy(indexOptions = indexOptions.some)
  def norms(norms: Boolean): DynamicField = copy(norms = norms.some)
  def termVector(termVector: String): DynamicField = copy(termVector = termVector.some)
  def store(store: Boolean): DynamicField = copy(store = store.some)
  def similarity(similarity: String): DynamicField = copy(similarity = similarity.some)
}
