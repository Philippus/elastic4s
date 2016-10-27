package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.analyzers.{CommonGramsTokenFilter, EdgeNGramTokenFilter, NGramTokenFilter, ShingleTokenFilter, SnowballTokenFilter, StemmerTokenFilter}
import com.sksamuel.elastic4s.query.{FuzzyQueryDefinition, IdQueryDefinition, IndicesQueryDefinition}

// a dumping ground for deprecated syntax, keeps the main file clear
trait DeprecatedElasticDsl {
  self: QueryDsl =>

  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: String*): IdQueryDefinition = IdQueryDefinition(ids.toSeq)

  @deprecated("use scoreSort, geoSort, fieldSort or scriptSort", "1.6.0")
  case object by {
    def score: ScoreSortDefinition = ElasticDsl.score.sort
    def geo(field: String): GeoDistanceSortDefinition = ElasticDsl.geo sort field
    def field(field: String): FieldSortDefinition = ElasticDsl.field.sort(field)
    def script(script: String) = ElasticDsl.script.sort(script)
  }

  @deprecated("use commonQuery(field", "3.0.0")
  def commonQuery = new CommonQueryExpectsField

  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }

  @deprecated("Fuzzy queries are not useful enough and will be removed in a future version", "3.0.0")
  def fuzzyQuery(name: String, value: Any) = FuzzyQueryDefinition(name, value)

  @deprecated("instead search on the `_index` field")
  def indicesQuery(indices: String*) = new {
    @deprecated("instead search on the `_index` field")
    def query(query: QueryDefinition): IndicesQueryDefinition = IndicesQueryDefinition(indices, query)
  }

  @deprecated("prefer the method commonGramsTokenFilter(\"name\")", "2.0.0")
  case object commonGrams {
    @deprecated("prefer the method commonGramsTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)
  }

  @deprecated("prefer the method edgeNGramTokenFilter(\"name\")", "2.0.0")
  case object edgeNGram {
    @deprecated("prefer the method edgeNGramTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)
  }
  @deprecated("prefer the method edgeNGramTokenFilter(\"name\") <-- note capitalization", "2.0.0")
  def edgeNGramTokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)

  @deprecated("prefer the method ngramTokenFilter(\"name\")", "2.0.0")
  case object ngram {
    @deprecated("prefer the method ngramTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): NGramTokenFilter = NGramTokenFilter(name)
  }

  @deprecated("use optimizeIndex(index)", "1.6.2")
  def optimize(indexes: String*): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)

  @deprecated("prefer the method shingleTokenFilter(\"name\")", "2.0.0")
  case object shingle {
    @deprecated("prefer the method shingleTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)
  }

  @deprecated("prefer the method snowballTokenFilter(\"name\")", "2.0.0")
  case object snowball {
    @deprecated("prefer the method snowballTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)
  }

  @deprecated("use score sort, geo sort, field sort or script sort", "1.6.1")
  case object sortby {
    def score: ScoreSortDefinition = new ScoreSortDefinition
    def geo(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
    def field(field: String): FieldSortDefinition = FieldSortDefinition(field)
    def script(script: ScriptDefinition) = ElasticDsl.script.sort(script)
  }

  @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
  case object stemmer {
    @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
  }
}
