package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class NestedField(name: String,
                       dynamic: Option[String] = None,
                       enabled: Option[Boolean] = None,
                       properties: Seq[ElasticField] = Nil,
                       includeInParent: Option[Boolean] = None,
                       includeInRoot: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "nested"
  def dynamic(d: Boolean): NestedField = dynamic(d.toString)
  def dynamic(d: String): NestedField = copy(dynamic = d.some)
  def includeInRoot(includeInRoot: Boolean): NestedField = copy(includeInRoot = includeInRoot.some)
  def includeInParent(includeInParent: Boolean): NestedField = copy(includeInParent = includeInParent.some)

  def fields(fields: ElasticField*): NestedField = copy(properties = fields.toList)
  def fields(fields: Iterable[ElasticField]): NestedField = copy(properties = fields.toList)

  def properties(properties: ElasticField*): NestedField = copy(properties = properties.toList)
  def properties(properties: Iterable[ElasticField]): NestedField = copy(properties = properties.toList)
}
