package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.exts.OptionImplicits._

case class WildcardField(name: String,
                         copyTo: Seq[String] = Nil,
                         enabled: Option[Boolean] = None,
                         fields: Seq[FieldDefinition] = Nil,
                         ignoreAbove: Option[Int] = None,
                         nulls: Nulls = Nulls())
    extends FieldDefinition {

  type T = WildcardField
  override def `type` = "wildcard"

  override def nullable(nullable: Boolean): T = copy(nulls = nulls.copy(nullable = nullable.some))
  override def nullValue(nullvalue: Any): T   = copy(nulls = nulls.copy(nullValue = nullvalue.some))

  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T     = copy(copyTo = copyTo.toSeq)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)

  def ignoreAbove(ignoreAbove: Int): T = copy(ignoreAbove = ignoreAbove.some)

  // As of version 7.9, Elasticsearch only supports the ignore_above and null_value
  // parameters on wildcard fields.
  // See https://www.elastic.co/guide/en/elasticsearch/reference/7.9/keyword.html#wildcard-field-type
  override val analysis = Analysis()
  override val boost: Option[Double] = None
  override val docValues: Option[Boolean] = None
  override val index: Option[String] = None
  override val norms: Option[Boolean] = None
  override val store: Option[Boolean] = None
  override val termVector: Option[String] = None

  override def analyzer(analyzer: String): T                      = this
  override def boost(boost: Double): WildcardField                = this
  override def docValues(docValues: Boolean): WildcardField       = this
  override def includeInAll(includeInAll: Boolean): WildcardField = this
  override def index(index: Boolean): T                           = this
  override def normalizer(normalizer: String): T                  = this
  override def norms(norms: Boolean): T                           = this
  override def searchAnalyzer(analyzer: String): T                = this
  override def store(b: Boolean): WildcardField                   = this
  override def termVector(t: String): WildcardField               = this
}
