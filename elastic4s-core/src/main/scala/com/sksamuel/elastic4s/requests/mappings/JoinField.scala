package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.exts.OptionImplicits._

case class JoinField(name: String,
                     analysis: Analysis = Analysis(),
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
                     nulls: Nulls = Nulls(),
                     store: Option[Boolean] = None,
                     termVector: Option[String] = None,
                     relations: Map[String, Any] = Map.empty)
    extends FieldDefinition {

  type T = JoinField
  override def `type` = "join"

  override def boost(boost: Double): T          = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)
  def dynamic(dynamic: String): T               = copy(dynamic = dynamic.some)
  def dynamic(dynamic: Boolean): T              = copy(dynamic = dynamic.toString.some)

  def relation(parent: String, child: String): T         = copy(relations = relations + (parent -> child))
  def relation(parent: String, children: Seq[String]): T = copy(relations = relations + (parent -> children))
  def relations(map: Map[String, Any]): T                = copy(relations = map)

  override def analyzer(analyzer: String): T       = copy(analysis = analysis.copy(analyzer = analyzer.some))
  override def normalizer(normalizer: String): T   = copy(analysis = analysis.copy(normalizer = normalizer.some))
  override def searchAnalyzer(analyzer: String): T = copy(analysis = analysis.copy(searchAnalyzer = analyzer.some))

  override def nullable(nullable: Boolean): T = copy(nulls = nulls.copy(nullable = nullable.some))
  override def nullValue(nullvalue: Any): T   = copy(nulls = nulls.copy(nullValue = nullvalue.some))

  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T     = copy(copyTo = copyTo.toSeq)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)

  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)

  override def norms(norms: Boolean): T = copy(norms = norms.some)

  override def store(b: Boolean): T = copy(store = b.some)

  override def termVector(t: String): T = copy(termVector = t.some)
}

case class Parent(parentType: String)

case class Child(childType: String, parentId: String)
