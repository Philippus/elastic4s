package com.sksamuel.elastic4s.fields

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

object WildcardField {
  val `type`: String = "wildcard"
}
// As of version 7.9, Elasticsearch only supports the ignore_above and null_value
// parameters on wildcard fields.
// See https://www.elastic.co/guide/en/elasticsearch/reference/7.9/keyword.html#wildcard-field-type
case class WildcardField(override val name: String,
                         ignoreAbove: Option[Int] = None,
                         nullValue: Option[String] = None) extends ElasticField {
  override def `type`: String = WildcardField.`type`

  def ignoreAbove(ignoreAbove: Int): WildcardField = copy(ignoreAbove = ignoreAbove.some)

  def nullValue(nullValue: String): WildcardField = copy(nullValue = nullValue.some)
}
