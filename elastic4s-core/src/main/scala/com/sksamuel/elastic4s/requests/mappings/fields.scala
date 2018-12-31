package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

trait FieldDefinition {

  type T <: FieldDefinition

  def name: String
  def `type`: String
  def analysis: Analysis
  def analyzer: Option[String] = analysis.analyzer
  def boost: Option[Double]
  def copyTo: Seq[String]
  def docValues: Option[Boolean]
  def enabled: Option[Boolean]
  def fields: Seq[FieldDefinition]
  def index: Option[String]
  def norms: Option[Boolean]
  def normalizer: Option[String] = analysis.normalizer

  def nulls: Nulls
  def nullable: Option[Boolean] = nulls.nullable
  def nullValue: Option[Any]    = nulls.nullValue

  def store: Option[Boolean]
  def searchAnalyzer: Option[String] = analysis.searchAnalyzer
  def termVector: Option[String]

  final def analyzer(a: Analyzer): T = analyzer(a.name)
  def analyzer(analyzer: String): T

  def boost(boost: Double): T
  def docValues(docValues: Boolean): T

  def enabled(enabled: Boolean): T

  final def fields(first: FieldDefinition, rest: FieldDefinition*): T = fields(first +: rest)
  def fields(fields: Iterable[FieldDefinition]): T

  def includeInAll(includeInAll: Boolean): T

  def index(index: Boolean): T

  def norms(norms: Boolean): T
  def normalizer(normalizer: String): T

  def nullable(nullable: Boolean): T
  def nullValue(nullvalue: Any): T

  def copyTo(first: String, rest: String*): T
  def copyTo(copyTo: Iterable[String]): T

  def store(b: Boolean): T
  final def stored(b: Boolean): T = store(b)

  def termVector(t: String): T

  final def searchAnalyzer(alyzer: Analyzer): T = searchAnalyzer(alyzer.name)
  def searchAnalyzer(analyzer: String): T
}

case class BasicField(name: String,
                      `type`: String,
                      analysis: Analysis = Analysis(),
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      docValues: Option[Boolean] = None,
                      enabled: Option[Boolean] = None,
                      fields: Seq[FieldDefinition] = Nil,
                      format: Option[String] = None,
                      includeInAll: Option[Boolean] = None,
                      ignoreAbove: Option[Int] = None,
                      ignoreMalformed: Option[Boolean] = None,
                      index: Option[String] = None,
                      indexOptions: Option[String] = None,
                      norms: Option[Boolean] = None,
                      nulls: Nulls = Nulls(),
                      scalingFactor: Option[Double] = None,
                      similarity: Option[String] = None,
                      store: Option[Boolean] = None,
                      termVector: Option[String] = None)
    extends FieldDefinition {

  type T = BasicField

  override def analyzer(analyzer: String): T       = copy(analysis = analysis.copy(analyzer = analyzer.some))
  override def normalizer(normalizer: String): T   = copy(analysis = analysis.copy(normalizer = normalizer.some))
  override def searchAnalyzer(analyzer: String): T = copy(analysis = analysis.copy(searchAnalyzer = analyzer.some))

  override def boost(boost: Double): T          = copy(boost = boost.some)
  override def docValues(docValues: Boolean): T = copy(docValues = docValues.some)

  override def fields(fields: Iterable[FieldDefinition]): T = copy(fields = fields.toSeq)

  def coerce(coerce: Boolean): T                       = copy(coerce = coerce.some)
  override def copyTo(first: String, rest: String*): T = copyTo(first +: rest)
  override def copyTo(copyTo: Iterable[String]): T     = copy(copyTo = copyTo.toSeq)

  override def enabled(enabled: Boolean): T = copy(enabled = enabled.some)

  def format(format: String): T = copy(format = format.some)

  def scalingFactor(scalingFactor: Double): T = copy(scalingFactor = scalingFactor.some)

  def similarity(similarity: String): T = copy(similarity = similarity.some)

  def ignoreAbove(ignoreAbove: Int): T                = copy(ignoreAbove = ignoreAbove.some)
  def ignoreMalformed(ignoreMalformed: Boolean): T    = copy(ignoreMalformed = ignoreMalformed.some)
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)

  override def index(index: Boolean): T = copy(index = index.toString.some)
  def index(index: String): BasicField = {
    require(`type` == "string")
    copy(index = index.some)
  }

  override def norms(norms: Boolean): T       = copy(norms = norms.some)
  override def nullable(nullable: Boolean): T = copy(nulls = nulls.copy(nullable = nullable.some))
  override def nullValue(nullvalue: Any): T   = copy(nulls = nulls.copy(nullValue = nullvalue.some))

  override def store(b: Boolean): T = copy(store = b.some)

  override def termVector(t: String): T = copy(termVector = t.some)
}

case class FielddataFrequencyFilter(min: Double, max: Double, minSegmentSize: Int)
