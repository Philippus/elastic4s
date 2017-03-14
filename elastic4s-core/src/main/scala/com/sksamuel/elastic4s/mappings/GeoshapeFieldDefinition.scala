package com.sksamuel.elastic4s.mappings

import com.sksamuel.exts.OptionImplicits._

case class GeoshapeFieldDefinition(name: String,
                                   analyzer: Option[String] = None,
                                   boost: Option[Double] = None,
                                   coerce: Option[Boolean] = None,
                                   copyTo: Seq[String] = Nil,
                                   docValues: Option[Boolean] = None,
                                   enabled: Option[Boolean] = None,
                                   fields: Seq[FieldDefinition] = Nil,
                                   format: Option[String] = None,
                                   ignoreMalformed: Option[Boolean] = None,
                                   includeInAll: Option[Boolean] = None,
                                   index: Option[String] = None,
                                   norms: Option[Boolean] = None,
                                   normalizer: Option[String] = None,
                                   nullable: Option[Boolean] = None,
                                   nullValue: Option[Any] = None,
                                   similarity: Option[String] = None,
                                   store: Option[Boolean] = None,
                                   tree: Option[String] = None,
                                   precision: Option[String] = None,
                                   strategy: Option[String] = None,
                                   distanceErrorPct: Option[Double] = None,
                                   orientation: Option[String] = None,
                                   pointsOnly: Option[Boolean] = None,
                                   searchAnalyzer: Option[String] = None,
                                   termVector: Option[String] = None,
                                   treeLevels: Option[String] = None) extends FieldDefinition {

  type T = GeoshapeFieldDefinition

  def `type`: String = "geo_shape"

  override def analyzer(analyzer: String): T = copy(analyzer = analyzer.some)
  override def boost(boost: Double): T = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)
  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T = copy(copyTo = copyTo.toSeq)

  def format(format: String): T = copy(format = format.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def norms(norms: Boolean): T = copy(norms = norms.some)
  override def normalizer(normalizer: String): T = copy(normalizer = normalizer.some)
  override def nullable(nullable: Boolean): T = copy(nullable = nullable.some)
  override def nullValue(nullvalue: Any): T = copy(nullValue = nullvalue.some)

  override def searchAnalyzer(analyzer: String): T = copy(searchAnalyzer = analyzer.some)
  override def store(b: Boolean): T = copy(store = b.some)
   def similarity(similarity: String): T = copy(similarity = similarity.some)

  override def termVector(t: String): T = copy(termVector = t.some)

  def tree(tree: String): GeoshapeFieldDefinition = copy(tree = tree.some)
  def precision(precision: String): GeoshapeFieldDefinition = copy(precision = precision.some)
  def strategy(strategy: String): GeoshapeFieldDefinition = copy(strategy = strategy.some)
  def distanceErrorPct(distanceErrorPct: Double): GeoshapeFieldDefinition = copy(distanceErrorPct = distanceErrorPct.some)
  def orientation(orientation: String): GeoshapeFieldDefinition = copy(orientation = orientation.some)
  def pointsOnly(pointsOnly: Boolean): GeoshapeFieldDefinition = copy(pointsOnly = pointsOnly.some)
  def treeLevels(treeLevels: String): GeoshapeFieldDefinition = copy(treeLevels = treeLevels.some)
}
