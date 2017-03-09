package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

trait FieldDefinition {

  type T <: FieldDefinition

  def name: String
  def `type`: String
  def analyzer: Option[String]
  def boost: Option[Double]
  def coerce: Option[Boolean]
  def copyTo: Seq[String]
  def docValues: Option[Boolean]
  def enabled: Option[Boolean]
  def fielddata: Option[Boolean]
  def fields: Seq[FieldDefinition]
  def format: Option[String]
  def index: Option[String]
  def ignoreMalformed: Option[Boolean]
  def includeInAll: Option[Boolean]
  def norms: Option[Boolean]
  def normalizer: Option[String]
  def nullValue: Option[Any]
  def similarity: Option[String]
  def store: Option[Boolean]
  def searchAnalyzer: Option[String]
  def termVector: Option[String]

  final def analyzer(a: Analyzer): T = analyzer(a.name)
  def analyzer(analyzer: String): T

  def boost(boost: Double): T
  def docValues(docValues: Boolean): T

  def enabled(enabled: Boolean): T

  final def fields(first: FieldDefinition, rest: FieldDefinition*): T = fields(first +: rest)
  def fields(fields: Iterable[FieldDefinition]): T

  def fielddata(fielddata: Boolean): T
  def format(format: String): T

  def ignoreMalformed(ignoreMalformed: Boolean): T
  def includeInAll(includeInAll: Boolean): T

  def index(index: Boolean): T

  def norms(norms: Boolean): T
  def normalizer(normalizer: String): T
  def nullable(nullable: Boolean): T
  def nullValue(nullvalue: Any): T

  def coerce(coerce: Boolean): T
  def copyTo(first: String, rest: String*): T
  def copyTo(copyTo: Iterable[String]): T

  def store(b: Boolean): T
  final def stored(b: Boolean): T = store(b)
  def similarity(similarity: String): T

  def termVector(t: String): T

  final def searchAnalyzer(alyzer: Analyzer): T = searchAnalyzer(alyzer.name)
  def searchAnalyzer(analyzer: String): T
}
case class GeoshapeFieldDefinition(name: String,
                                   analyzer: Option[String] = None,
                                   boost: Option[Double] = None,
                                   coerce: Option[Boolean] = None,
                                   copyTo: Seq[String] = Nil,
                                   docValues: Option[Boolean] = None,
                                   enabled: Option[Boolean] = None,
                                   fields: Seq[FieldDefinition] = Nil,
                                   fielddata: Option[Boolean] = None,
                                   fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
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

  override def coerce(coerce: Boolean): T = copy(coerce = coerce.some)
  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T = copy(copyTo = copyTo.toSeq)

  override def fielddata(fielddata: Boolean): T = copy(fielddata = fielddata.some)
  override def format(format: String): T = copy(format = format.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)
  override def ignoreMalformed(ignoreMalformed: Boolean): T = copy(ignoreMalformed = ignoreMalformed.some)
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def norms(norms: Boolean): T = copy(norms = norms.some)
  override def normalizer(normalizer: String): T = copy(normalizer = normalizer.some)
  override def nullable(nullable: Boolean): T = copy(nullable = nullable.some)
  override def nullValue(nullvalue: Any): T = copy(nullValue = nullvalue.some)

  override def searchAnalyzer(analyzer: String): T = copy(searchAnalyzer = analyzer.some)
  override def store(b: Boolean): T = copy(store = b.some)
  override def similarity(similarity: String): T = copy(similarity = similarity.some)

  override def termVector(t: String): T = copy(termVector = t.some)

  def tree(tree: String): GeoshapeFieldDefinition = copy(tree = tree.some)
  def precision(precision: String): GeoshapeFieldDefinition = copy(precision = precision.some)
  def strategy(strategy: String): GeoshapeFieldDefinition = copy(strategy = strategy.some)
  def distanceErrorPct(distanceErrorPct: Double): GeoshapeFieldDefinition = copy(distanceErrorPct = distanceErrorPct.some)
  def orientation(orientation: String): GeoshapeFieldDefinition = copy(orientation = orientation.some)
  def pointsOnly(pointsOnly: Boolean): GeoshapeFieldDefinition = copy(pointsOnly = pointsOnly.some)
  def treeLevels(treeLevels: String): GeoshapeFieldDefinition = copy(treeLevels = treeLevels.some)
}

case class BasicFieldDefinition(name: String,
                                `type`: String,
                                analyzer: Option[String] = None,
                                boost: Option[Double] = None,
                                coerce: Option[Boolean] = None,
                                copyTo: Seq[String] = Nil,
                                docValues: Option[Boolean] = None,
                                enabled: Option[Boolean] = None,
                                fields: Seq[FieldDefinition] = Nil,
                                fielddata: Option[Boolean] = None,
                                fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                                format: Option[String] = None,
                                includeInAll: Option[Boolean] = None,
                                ignoreAbove: Option[Int] = None,
                                ignoreMalformed: Option[Boolean] = None,
                                index: Option[String] = None,
                                indexOptions: Option[String] = None,
                                maxInputLength: Option[Int] = None,
                                norms: Option[Boolean] = None,
                                normalizer: Option[String] = None,
                                nullable: Option[Boolean] = None,
                                nullValue: Option[Any] = None,
                                positionIncrementGap: Option[Int] = None,
                                preserveSeparators: Option[Boolean] = None,
                                preservePositionIncrements: Option[Boolean] = None,
                                scalingFactor: Option[Double] = None,
                                searchAnalyzer: Option[String] = None,
                                similarity: Option[String] = None,
                                store: Option[Boolean] = None,
                                termVector: Option[String] = None
                               ) extends FieldDefinition {

  type T = BasicFieldDefinition

  override def analyzer(analyzer: String): T = copy(analyzer = analyzer.some)
  override def boost(boost: Double): T = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)

  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  override def coerce(coerce: Boolean): T = copy(coerce = coerce.some)
  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T = copy(copyTo = copyTo.toSeq)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)

  override def fielddata(fielddata: Boolean): T = copy(fielddata = fielddata.some)
  override def format(format: String): T = copy(format = format.some)

  def preserveSeparators(preserve: Boolean): T = copy(preserveSeparators = preserve.some)
  def preservePositionIncrements(preserve: Boolean): T = copy(preservePositionIncrements = preserve.some)

  def scalingFactor(scalingFactor: Double): T = copy(scalingFactor = scalingFactor.some)

  override def similarity(similarity: String): T = copy(similarity = similarity.some)

  def ignoreAbove(ignoreAbove: Int): T = copy(ignoreAbove = ignoreAbove.some)
  override def ignoreMalformed(ignoreMalformed: Boolean): T = copy(ignoreMalformed = ignoreMalformed.some)
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)
  def index(index: String): BasicFieldDefinition = {
    require(`type` == "string")
    copy(index = index.some)
  }

  def maxInputLength(maxInputLength: Int): T = copy(maxInputLength = maxInputLength.some)

  override def norms(norms: Boolean): T = copy(norms = norms.some)
  override def normalizer(normalizer: String): T = copy(normalizer = normalizer.some)
  override def nullable(nullable: Boolean): T = copy(nullable = nullable.some)
  override def nullValue(nullvalue: Any): T = copy(nullValue = nullvalue.some)

  override def store(b: Boolean): T = copy(store = b.some)
  override def searchAnalyzer(analyzer: String): T = copy(searchAnalyzer = analyzer.some)

  override def termVector(t: String): T = copy(termVector = t.some)
}

case class FielddataFrequencyFilter(min: Double, max: Double, minSegmentSize: Int)
