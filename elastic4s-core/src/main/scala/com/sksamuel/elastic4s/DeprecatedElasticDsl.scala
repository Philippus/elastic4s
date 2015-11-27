package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.analyzers.{StemmerTokenFilter, SnowballTokenFilter, ShingleTokenFilter, NGramTokenFilter, EdgeNGramTokenFilter, CommonGramsTokenFilter}

// a dumping ground for deprecated syntax, keeps the main file clear
trait DeprecatedElasticDsl {

  @deprecated("use scoreSort, geoSort, fieldSort or scriptSort", "1.6.0")
  case object by {
    def score: ScoreSortDefinition = ElasticDsl.score.sort
    def geo(field: String): GeoDistanceSortDefinition = ElasticDsl.geo sort field
    def field(field: String): FieldSortDefinition = ElasticDsl.field.sort(field)
    def script(script: String) = ElasticDsl.script.sort(script)
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
  def optimize(indexes: String*): OptimizeDefinition = new OptimizeDefinition(indexes.toSeq)

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
    def field(field: String): FieldSortDefinition = new FieldSortDefinition(field)
    def script(script: String): ScriptSortDefinition = new ScriptSortDefinition(script)
  }

  @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
  case object stemmer {
    @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
  }
}
