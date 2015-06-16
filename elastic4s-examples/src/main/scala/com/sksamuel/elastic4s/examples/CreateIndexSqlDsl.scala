package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings.FieldType.{IntegerType, StringType}

// examples of the count API in dot notation
class CreateIndexSqlDsl extends ElasticDsl {

  // create index with no mappings and with shards / replica sets
  create index "tweets" shards 3 replicas 4

  // create index with no mappings but refresh interval set
  create index "tweets" refreshInterval "5s"

  // create index with two mappings, each with two fields
  create index "index" mappings(
    "tweets" as(
      "name" typed StringType,
      "userId" typed IntegerType
      ),
    "users" as(
      "userId" typed IntegerType,
      "username" typed StringType
      )
    )

  // create index with copy_to functionaliy
  create index "tweets" mappings (
    "tweet" as(
      "title" typed StringType index "analyzed" copyTo("meta_data", "article_info"),
      )
    )

  // create index "users" with custom analyzers
  create index "users" analysis(
    PatternAnalyzerDefinition("patternAnalyzer", regex = "[a-z]"),
    SnowballAnalyzerDefinition("mysnowball", lang = "english", stopwords = Seq("stop1", "stop2", "stop3")),
    CustomAnalyzerDefinition(
      "myAnalyzer1",
      StandardTokenizer("myTokenizer1", 900),
      LengthTokenFilter("myTokenFilter2", 0, max = 10),
      UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
      StemmerTokenFilter("myFrenchStemmerTokenFilter", lang = "french"),
      PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep")
    ),
    CustomAnalyzerDefinition(
      "myAnalyzer2",
      NGramTokenizer("myTokenizer5", minGram = 4, maxGram = 18, tokenChars = Seq("letter", "punctuation"))
    ))
}
