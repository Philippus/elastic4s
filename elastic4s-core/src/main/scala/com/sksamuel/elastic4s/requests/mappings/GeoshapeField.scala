package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.exts.OptionImplicits._

case class GeoFields(tree: Option[String] = None,
                     precision: Option[String] = None,
                     strategy: Option[String] = None,
                     distanceErrorPct: Option[Double] = None,
                     orientation: Option[String] = None,
                     pointsOnly: Option[Boolean] = None,
                     treeLevels: Option[String] = None)

case class GeoshapeField(name: String,
                         analysis: Analysis = Analysis(),
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
                         nulls: Nulls = Nulls(),
                         similarity: Option[String] = None,
                         store: Option[Boolean] = None,
                         geoFields: GeoFields = GeoFields(),
                         termVector: Option[String] = None)
    extends FieldDefinition {

  type T = GeoshapeField

  def `type`: String = "geo_shape"

  override def boost(boost: Double): T          = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)

  override def analyzer(analyzer: String): T       = copy(analysis = analysis.copy(analyzer = analyzer.some))
  override def normalizer(normalizer: String): T   = copy(analysis = analysis.copy(normalizer = normalizer.some))
  override def searchAnalyzer(analyzer: String): T = copy(analysis = analysis.copy(searchAnalyzer = analyzer.some))

  override def enabled(enabled: Boolean): T                 = copy(enabled = enabled.some)
  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T     = copy(copyTo = copyTo.toSeq)

  def format(format: String): T = copy(format = format.some)

  override def index(index: Boolean): T               = copy(index = index.toString.some)
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def norms(norms: Boolean): T       = copy(norms = norms.some)
  override def nullable(nullable: Boolean): T = copy(nulls = nulls.copy(nullable = nullable.some))
  override def nullValue(nullvalue: Any): T   = copy(nulls = nulls.copy(nullValue = nullvalue.some))

  override def store(b: Boolean): T     = copy(store = b.some)
  def similarity(similarity: String): T = copy(similarity = similarity.some)

  override def termVector(t: String): T = copy(termVector = t.some)

  def tree(tree: String): GeoshapeField = copy(geoFields = geoFields.copy(tree = tree.some))
  def precision(precision: String): GeoshapeField =
    copy(geoFields = geoFields.copy(precision = precision.some))
  def strategy(strategy: String): GeoshapeField = copy(geoFields = geoFields.copy(strategy = strategy.some))
  def distanceErrorPct(distanceErrorPct: Double): GeoshapeField =
    copy(geoFields = geoFields.copy(distanceErrorPct = distanceErrorPct.some))
  def orientation(orientation: String): GeoshapeField =
    copy(geoFields = geoFields.copy(orientation = orientation.some))
  def pointsOnly(pointsOnly: Boolean): GeoshapeField =
    copy(geoFields = geoFields.copy(pointsOnly = pointsOnly.some))
  def treeLevels(treeLevels: String): GeoshapeField =
    copy(geoFields = geoFields.copy(treeLevels = treeLevels.some))
}
