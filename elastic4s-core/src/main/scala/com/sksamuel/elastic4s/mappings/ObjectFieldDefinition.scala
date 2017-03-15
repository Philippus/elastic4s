package com.sksamuel.elastic4s.mappings

import com.sksamuel.exts.OptionImplicits._

case class ObjectFieldDefinition(name: String,
                                 analyzer: Option[String] = None,
                                 boost: Option[Double] = None,
                                 copyTo: Seq[String] = Nil,
                                 docValues: Option[Boolean] = None,
                                 dynamic: Option[String] = None,
                                 enabled: Option[Boolean] = None,
                                 includeInAll: Option[Boolean] = None,
                                 index: Option[String] = None,
                                 indexOptions: Option[String] = None,
                                 fields: Seq[FieldDefinition] = Nil,
                                 norms: Option[Boolean] = None,
                                 normalizer: Option[String] = None,
                                 nullable: Option[Boolean] = None,
                                 nullValue: Option[Any] = None,
                                 searchAnalyzer: Option[String] = None,
                                 store: Option[Boolean] = None,
                                 termVector: Option[String] = None
                                ) extends FieldDefinition {

  type T = ObjectFieldDefinition
  override def `type` = "object"

  override def analyzer(analyzer: String): T = copy(analyzer = analyzer.some)
  override def boost(boost: Double): T = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)
  def dynamic(dynamic: String): T = copy(dynamic = dynamic.some)
  def dynamic(dynamic: Boolean): T = copy(dynamic = dynamic.toString.some)

  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T = copy(copyTo = copyTo.toSeq)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)

  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)

  override def norms(norms: Boolean): T = copy(norms = norms.some)
  override def normalizer(normalizer: String): T = copy(normalizer = normalizer.some)
  override def nullable(nullable: Boolean): T = copy(nullable = nullable.some)
  override def nullValue(nullvalue: Any): T = copy(nullValue = nullvalue.some)

  override def store(b: Boolean): T = copy(store = b.some)
  override def searchAnalyzer(analyzer: String): T = copy(searchAnalyzer = analyzer.some)

  override def termVector(t: String): T = copy(termVector = t.some)
}
