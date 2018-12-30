package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.exts.OptionImplicits._

case class Analysis(analyzer: Option[String] = None,
                    searchAnalyzer: Option[String] = None,
                    normalizer: Option[String] = None)

case class Ignores(ignoreAbove: Option[Int] = None, ignoreMalformed: Option[Boolean] = None)

case class Nulls(nullable: Option[Boolean] = None, nullValue: Option[Any] = None)

case class CompletionField(name: String,
                           analysis: Analysis = Analysis(),
                           boost: Option[Double] = None,
                           coerce: Option[Boolean] = None,
                           copyTo: Seq[String] = Nil,
                           docValues: Option[Boolean] = None,
                           enabled: Option[Boolean] = None,
                           fields: Seq[FieldDefinition] = Nil,
                           fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                           includeInAll: Option[Boolean] = None,
                           ignores: Ignores = Ignores(),
                           index: Option[String] = None,
                           indexOptions: Option[String] = None,
                           maxInputLength: Option[Int] = None,
                           norms: Option[Boolean] = None,
                           nulls: Nulls = Nulls(),
                           preserveSeparators: Option[Boolean] = None,
                           preservePositionIncrements: Option[Boolean] = None,
                           similarity: Option[String] = None,
                           store: Option[Boolean] = None,
                           termVector: Option[String] = None,
                           contexts: Seq[ContextField] = Nil)
    extends FieldDefinition {

  type T = CompletionField
  override def `type` = "completion"

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

  def similarity(similarity: String): T = copy(similarity = similarity.some)

  def ignoreAbove(ignoreAbove: Int): T = copy(ignores = ignores.copy(ignoreAbove = ignoreAbove.some))
  def ignoreMalformed(ignoreMalformed: Boolean): T =
    copy(ignores = ignores.copy(ignoreMalformed = ignoreMalformed.some))
  override def includeInAll(includeInAll: Boolean): T = copy(includeInAll = includeInAll.some)
  override def index(index: Boolean): T               = copy(index = index.toString.some)

  override def norms(norms: Boolean): T       = copy(norms = norms.some)
  override def nullable(nullable: Boolean): T = copy(nulls = nulls.copy(nullable = nullable.some))
  override def nullValue(nullvalue: Any): T   = copy(nulls = nulls.copy(nullValue = nullvalue.some))

  override def store(b: Boolean): T = copy(store = b.some)

  override def termVector(t: String): T = copy(termVector = t.some)

  def preserveSeparators(preserve: Boolean): T         = copy(preserveSeparators = preserve.some)
  def preservePositionIncrements(preserve: Boolean): T = copy(preservePositionIncrements = preserve.some)
  def maxInputLength(maxInputLength: Int): T           = copy(maxInputLength = maxInputLength.some)

  def contexts(first: ContextField, rest: ContextField*): CompletionField = contexts(first +: rest)
  def contexts(contexts: Iterable[ContextField]): CompletionField         = copy(contexts = this.contexts ++ contexts)
}

case class ContextField(name: String, `type`: String, path: Option[String] = None, precision: Option[Int] = None) {
  def path(path: String): ContextField =
    copy(path = path.some)

  def precision(precision: Int): ContextField =
    copy(precision = precision.some)
}
