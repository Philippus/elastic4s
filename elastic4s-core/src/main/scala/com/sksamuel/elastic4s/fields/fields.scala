package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.requests.mappings.FielddataFrequencyFilter





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








case class JoinField(name: String,
                     eagerGlobalOrdinals: Option[Boolean] = None,
                     relations: Map[String, Any] = Map.empty,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "join"
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





















case class DateRangeField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          format: Option[String] = None,
                          store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "date_range"
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


case class IndexPrefixes(minChars: Int, maxChars: Int)

case class RankFeatureField(name: String,
                            positiveScoreImpact: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "rank_feature"
}

case class RankFeaturesField(name: String) extends ElasticField {
  override def `type`: String = "rank_features"
}





case class AnnotatedTextField(name: String) extends ElasticField {
  override def `type`: String = "annotated_text"
}

case class PercolatorField(name: String) extends ElasticField {
  override def `type`: String = "percolator"
}


