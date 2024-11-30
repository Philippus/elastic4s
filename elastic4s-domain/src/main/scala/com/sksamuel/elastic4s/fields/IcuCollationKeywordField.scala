package com.sksamuel.elastic4s.fields

// https://www.elastic.co/guide/en/elasticsearch/plugins/master/analysis-icu-collation-keyword-field.html
case class IcuCollationKeywordField(
    name: String,
    language: Option[String] = None,
    country: Option[String] = None,
    variant: Option[String] = None,
    strength: Option[String] = None,
    decomposition: Option[String] = None,
    alternate: Option[String] = None,
    caseLevel: Option[Boolean] = None,
    caseFirst: Option[String] = None,
    numeric: Option[Boolean] = None,
    variableTop: Option[String] = None,
    hiraganaQuaternaryMode: Option[Boolean] = None,
    fields: List[ElasticField] = Nil,
    index: Option[Boolean] = None,
    docValues: Option[Boolean] = None,
    ignoreAbove: Option[Int] = None,
    nullValue: Option[String] = None,
    store: Option[Boolean] = None
) extends ElasticField {
  override def `type`: String = "icu_collation_keyword"
}
