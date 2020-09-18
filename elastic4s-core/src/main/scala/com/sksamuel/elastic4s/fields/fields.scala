package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.requests.mappings.{ContextField, FielddataFrequencyFilter}

sealed trait ElasticField {
  def name: String
  def `type`: String // defines the elasticsearch constant used for this type, eg "integer" or "geo_shape"
}

case class ConstantKeywordField(override val name: String, value: String) extends ElasticField {
  override def `type`: String = "constant_keyword"
}

case class WildcardField(override val name: String,
                         ignoreAbove: Option[Int] = None) extends ElasticField {
  override def `type`: String = "wildcard"
}

case class TextField(override val name: String,
                     analyzer: Option[String] = None,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     fields: List[ElasticField] = Nil,
                     fielddata: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/fielddata.html
                     fielddataFrequencyFilter: Option[FielddataFrequencyFilter] = None,
                     ignoreAbove: Option[Int] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/ignore-above.html
                     index: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-index.html
                     indexPrefixes: Option[IndexPrefixes] = None,
                     indexPhrases: Option[Boolean] = None,
                     indexOptions: Option[String] = None,
                     norms: Option[Boolean] = None,
                     positionIncrementGap: Option[Int] = None,
                     searchAnalyzer: Option[String] = None,
                     searchQuoteAnalyzer: Option[String] = None,
                     similarity: Option[String] = None,
                     store: Option[Boolean] = None,
                     termVector: Option[String] = None,
                     meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "text"
  def analyzer(name:String): TextField = copy(analyzer = Option(name))
}

case class SearchAsYouTypeField(name: String,
                                analyzer: Option[String] = None,
                                boost: Option[Double] = None,
                                copyTo: Seq[String] = Nil,
                                docValues: Option[Boolean] = None, // https://www.elastic.co/guide/en/elasticsearch/reference/current/doc-values.html
                                fielddata: Option[Boolean] = None,
                                ignoreAbove: Option[Int] = None,
                                index: Option[Boolean] = None,
                                indexOptions: Option[String] = None,
                                maxShingleSize: Option[Int] = None,
                                norms: Option[Boolean] = None,
                                similarity: Option[String] = None,
                                store: Option[Boolean] = None,
                                termVector: Option[String] = None,
                                meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "search_as_you_type"
}

case class FlattenedField(name: String,
                          boost: Option[Double] = None,
                          docValues: Option[Boolean] = None,
                          depthLimit: Option[Int] = None,
                          eagerGlobalOrdinals: Option[Boolean] = None,
                          ignoreAbove: Option[Int] = None,
                          index: Option[Boolean] = None,
                          indexOptions: Option[String] = None,
                          nullValue: Option[String] = None,
                          similarity: Option[String] = None,
                          splitQueriesOnWhitespace: Option[Boolean] = None,
                          meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "flattened"
}

case class KeywordField(name: String,
                        boost: Option[Double] = None,
                        copyTo: Seq[String] = Nil,
                        docValues: Option[Boolean] = None,
                        eagerGlobalOrdinals: Option[Boolean] = None,
                        fields: List[ElasticField] = Nil,
                        ignoreAbove: Option[Int] = None,
                        index: Option[Boolean] = None,
                        indexOptions: Option[String] = None,
                        norms: Option[Boolean] = None,
                        normalizer: Option[String] = None,
                        nullValue: Option[String] = None,
                        similarity: Option[String] = None,
                        splitQueriesOnWhitespace: Option[Boolean] = None,
                        store: Option[Boolean] = None,
                        termVector: Option[String] = None,
                        meta: Map[String, String] = Map.empty) extends ElasticField {
  override def `type`: String = "keyword"
}

case class GeoPointField(name: String,
                         boost: Option[Double] = None,
                         copyTo: Seq[String] = Nil,
                         docValues: Option[Boolean] = None,
                         ignoreMalformed: Option[Boolean] = None,
                         ignoreZValue: Option[Boolean] = None,
                         index: Option[Boolean] = None,
                         norms: Option[Boolean] = None,
                         nullValue: Option[String] = None,
                         store: Option[Boolean] = None,
                         meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "geo_point"
}

case class GeoShapeField(name: String,
                         boost: Option[Double] = None,
                         copyTo: Seq[String] = Nil,
                         docValues: Option[Boolean] = None,
                         ignoreMalformed: Option[Boolean] = None,
                         ignoreZValue: Option[Boolean] = None,
                         index: Option[Boolean] = None,
                         norms: Option[Boolean] = None,
                         nullValue: Option[String] = None,
                         store: Option[Boolean] = None,
                         tree: Option[String] = None,
                         precision: Option[String] = None,
                         strategy: Option[String] = None,
                         distanceErrorPct: Option[Double] = None,
                         orientation: Option[String] = None,
                         pointsOnly: Option[Boolean] = None,
                         treeLevels: Option[String] = None,
                         meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "geo_shape"
}

case class ShapeField(name: String,
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      ignoreMalformed: Option[Boolean] = None,
                      ignoreZValue: Option[Boolean] = None,
                      index: Option[Boolean] = None,
                      norms: Option[Boolean] = None,
                      nullValue: Option[String] = None,
                      store: Option[Boolean] = None,
                      orientation: Option[String] = None,
                      meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "shape"
}

case class CompletionField(name: String,
                           analyzer: Option[String] = None,
                           boost: Option[Double] = None,
                           copyTo: Seq[String] = Nil,
                           index: Option[Boolean] = None,
                           indexOptions: Option[String] = None,
                           ignoreAbove: Option[Int] = None,
                           ignoreMalformed: Option[Boolean] = None,
                           maxInputLength: Option[Int] = None,
                           norms: Option[Boolean] = None,
                           nullValue: Option[String] = None,
                           preserveSeparators: Option[Boolean] = None,
                           preservePositionIncrements: Option[Boolean] = None,
                           similarity: Option[String] = None,
                           searchAnalyzer: Option[String] = None,
                           store: Option[Boolean] = None,
                           termVector: Option[String] = None,
                           contexts: Seq[ContextField] = Nil,
                           meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "completion"
}

case class TokenCountField(name: String,
                           analyzer: Option[String] = None,
                           boost: Option[Double] = None,
                           copyTo: Seq[String] = Nil,
                           docValues: Option[Boolean] = None,
                           enablePositionIncrements: Option[Boolean] = None,
                           index: Option[Boolean] = None,
                           nullValue: Option[String] = None,
                           store: Option[Boolean] = None,
                           meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "token_count"
}


case class AliasField(name: String,
                      path: String) extends ElasticField {
  override def `type`: String = "alias"
}

case class DenseVectorField(name: String,
                            dims: Int) extends ElasticField {
  override def `type`: String = "dense_vector"
}

case class JoinField(name: String,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     relations: Map[String, Any] = Map.empty,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "join"
}

sealed trait NumberField[T] extends ElasticField {
  def boost: Option[Double]
  def coerce: Option[Boolean]
  def ignoreMalformed: Option[Boolean]
  def index: Option[Boolean]
  def store: Option[Boolean]
  def docValues: Option[Boolean]
  def nullValue: Option[T]
  def copyTo: Seq[String]
}

case class LongField(name: String,
                     boost: Option[Double] = None,
                     coerce: Option[Boolean] = None,
                     copyTo: Seq[String] = Nil,
                     docValues: Option[Boolean] = None,
                     ignoreMalformed: Option[Boolean] = None,
                     index: Option[Boolean] = None,
                     store: Option[Boolean] = None,
                     nullValue: Option[Long] = None,
                     meta: Map[String, Any] = Map.empty) extends NumberField[Long] {
  override def `type`: String = "long"
}

case class IntegerField(name: String,
                        boost: Option[Double] = None,
                        coerce: Option[Boolean] = None,
                        copyTo: Seq[String] = Nil,
                        docValues: Option[Boolean] = None,
                        ignoreMalformed: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        nullValue: Option[Int] = None,
                        store: Option[Boolean] = None,
                        meta: Map[String, Any] = Map.empty) extends NumberField[Int] {
  override def `type`: String = "integer"
}

case class DoubleField(name: String,
                       boost: Option[Double] = None,
                       coerce: Option[Boolean] = None,
                       copyTo: Seq[String] = Nil,
                       docValues: Option[Boolean] = None,
                       ignoreMalformed: Option[Boolean] = None,
                       index: Option[Boolean] = None,
                       nullValue: Option[Double] = None,
                       store: Option[Boolean] = None,
                       meta: Map[String, Any] = Map.empty) extends NumberField[Double] {
  override def `type`: String = "double"
}

case class FloatField(name: String,
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      docValues: Option[Boolean] = None,
                      ignoreMalformed: Option[Boolean] = None,
                      index: Option[Boolean] = None,
                      nullValue: Option[Float] = None,
                      store: Option[Boolean] = None,
                      meta: Map[String, Any] = Map.empty) extends NumberField[Float] {
  override def `type`: String = "float"
}

case class HalfFloatField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          copyTo: Seq[String] = Nil,
                          docValues: Option[Boolean] = None,
                          ignoreMalformed: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          nullValue: Option[Float] = None,
                          store: Option[Boolean] = None,
                          meta: Map[String, Any] = Map.empty) extends NumberField[Float] {
  override def `type`: String = "half_float"
}

case class ScaledFloatField(name: String,
                            boost: Option[Double] = None,
                            coerce: Option[Boolean] = None,
                            copyTo: Seq[String] = Nil,
                            docValues: Option[Boolean] = None,
                            ignoreMalformed: Option[Boolean] = None,
                            scalingFactor: Option[Int] = None,
                            index: Option[Boolean] = None,
                            nullValue: Option[Float] = None,
                            store: Option[Boolean] = None,
                            meta: Map[String, Any] = Map.empty) extends NumberField[Float] {
  override def `type`: String = "scaled_float"
}

case class ShortField(name: String,
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      docValues: Option[Boolean] = None,
                      enabled: Option[Boolean] = None,
                      ignoreMalformed: Option[Boolean] = None,
                      index: Option[Boolean] = None,
                      nullValue: Option[Short] = None,
                      store: Option[Boolean] = None,
                      meta: Map[String, Any] = Map.empty) extends NumberField[Short] {
  override def `type`: String = "short"
}

case class ByteField(name: String,
                     boost: Option[Double] = None,
                     coerce: Option[Boolean] = None,
                     copyTo: Seq[String] = Nil,
                     docValues: Option[Boolean] = None,
                     ignoreMalformed: Option[Boolean] = None,
                     index: Option[Boolean] = None,
                     nullValue: Option[Byte] = None,
                     store: Option[Boolean] = None,
                     meta: Map[String, Any] = Map.empty) extends NumberField[Byte] {
  override def `type`: String = "byte"
}

case class BooleanField(name: String,
                        boost: Option[Double] = None,
                        copyTo: Seq[String] = Nil, // https://www.elastic.co/guide/en/elasticsearch/reference/current/copy-to.html
                        docValues: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        nullValue: Option[Boolean] = None,
                        store: Option[Boolean] = None,
                        meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "boolean"
}

case class DateField(name: String,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     docValues: Option[Boolean] = None,
                     format: Option[String] = None,
                     locale: Option[String] = None,
                     ignoreMalformed: Option[Boolean] = None,
                     index: Option[Boolean] = None,
                     nullValue: Option[String] = None,
                     store: Option[Boolean] = None,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "date"
}

case class DateNanosField(name: String,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     docValues: Option[Boolean] = None,
                     format: Option[String] = None,
                     locale: Option[String] = None,
                     ignoreMalformed: Option[Boolean] = None,
                     index: Option[Boolean] = None,
                     nullValue: Option[String] = None,
                     store: Option[Boolean] = None,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "date_nanos"
}

trait RangeField extends ElasticField {
  def boost: Option[Double]
  def coerce: Option[Boolean]
  def index: Option[Boolean]
  def store: Option[Boolean]
}

case class LongRangeField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "long_range"
}

case class IntegerRangeField(name: String,
                             boost: Option[Double] = None,
                             coerce: Option[Boolean] = None,
                             index: Option[Boolean] = None,
                             store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "integer_range"
}

case class DoubleRangeField(name: String,
                            boost: Option[Double] = None,
                            coerce: Option[Boolean] = None,
                            index: Option[Boolean] = None,
                            store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "double_range"
}

case class FloatRangeField(name: String,
                           boost: Option[Double] = None,
                           coerce: Option[Boolean] = None,
                           index: Option[Boolean] = None,
                           store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "float_range"
}

case class DateRangeField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          format: Option[String] = None,
                          store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "date_range"
}

case class IpRangeField(name: String,
                        boost: Option[Double] = None,
                        coerce: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        format: Option[String] = None,
                        store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "ip_range"
}

case class BinaryField(name: String,
                       docValues: Option[Boolean] = None,
                       store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "binary"
}

case class ObjectField(name: String,
                       dynamic: Option[String] = None,
                       enabled: Option[Boolean] = None,
                       properties: Seq[ElasticField] = Nil) extends ElasticField {
  override def `type`: String = "object"
}

case class NestedField(name: String,
                       dynamic: Option[String] = None,
                       enabled: Option[Boolean] = None,
                       properties: Seq[ElasticField] = Nil,
                       includeInParent: Option[Boolean] = None,
                       includeInRoot: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "nested"
}

case class IpField(name: String,
                   boost: Option[Double] = None,
                   copyTo: Seq[String] = Nil,
                   docValues: Option[Boolean] = None,
                   index: Option[Boolean] = None,
                   properties: Seq[ElasticField] = Nil,
                   store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "ip"
}
case class IndexPrefixes(minChars: Int, maxChars: Int)

case class RankFeatureField(name: String,
                            positiveScoreImpact: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "rank_feature"
}

case class RankFeaturesField(name: String) extends ElasticField {
  override def `type`: String = "rank_features"
}

case class HistogramField(name: String) extends ElasticField {
  override def `type`: String = "histogram"
}

case class Murmur3Field(name: String) extends ElasticField {
  override def `type`: String = "murmur3"
}

case class AnnotatedTextField(name: String) extends ElasticField {
  override def `type`: String = "annotated_text"
}

case class PercolatorField(name: String) extends ElasticField {
  override def `type`: String = "percolator"
}
