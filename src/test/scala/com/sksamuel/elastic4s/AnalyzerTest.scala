package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.{ FreeSpec, Matchers }

class AnalyzerTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    create index "analyzer" mappings {
      "test" as (
        "keyword" typed StringType analyzer KeywordAnalyzer,
        "snowball" typed StringType analyzer SnowballAnalyzer,
        "whitespace" typed StringType analyzer WhitespaceAnalyzer,
        "stop" typed StringType analyzer StopAnalyzer,
        "standard1" typed StringType analyzer CustomAnalyzer("standard1"),
        "simple1" typed StringType analyzer SimpleAnalyzer,
        "pattern1" typed StringType analyzer CustomAnalyzer("pattern1"),
        "pattern2" typed StringType analyzer CustomAnalyzer("pattern2"),
        "ngram" typed StringType analyzer CustomAnalyzer("default_ngram"),
        "edgengram" withType StringType analyzer CustomAnalyzer("edgengram"),
        "custom_ngram" typed StringType indexAnalyzer CustomAnalyzer("my_ngram") searchAnalyzer KeywordAnalyzer
      )
    } analysis (
      PatternAnalyzerDefinition("pattern1", "\\d", false),
      PatternAnalyzerDefinition("pattern2", ",", false),
      CustomAnalyzerDefinition("default_ngram", NGramTokenizer),
      CustomAnalyzerDefinition("my_ngram",
        StandardTokenizer,
        LowercaseTokenFilter,
        NGramTokenFilter("my_ngram_filter", minGram = 2, maxGram = 5)),
        CustomAnalyzerDefinition("edgengram",
          StandardTokenizer,
          LowercaseTokenFilter,
          EdgeNGramTokenFilter("edgengram_filter", minGram = 2, maxGram = 6, side = "back")),
          CustomAnalyzerDefinition("standard1", StandardTokenizer("stokenizer1", 10))
    )
  }.await

  client.execute {
    index into "analyzer/test" fields (
      "keyword" -> "light as a feather",
      "snowball" -> "flying in the skies",
      "whitespace" -> "and and and qwerty uiop",
      "standard1" -> "aaaaaaaaaaa",
      "simple" -> "LOWER-CASED",
      "ngram" -> "starcraft",
      "custom_ngram" -> "dyson dc50i",
      "edgengram" -> "gameofthrones",
      "stop" -> "and and and",
      "pattern1" -> "abc123def",
      "pattern2" -> "jethro tull,coldplay"
    )
  }.await

  refresh("analyzer")
  blockUntilCount(1, "analyzer")

  "KeywordAnalyzer" - {
    "should index entire string as a single token" in {
      client.execute {
        search in "analyzer/test" query termQuery("keyword" -> "feather")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "default NGramTokenizer" - {
    "should index 2 combinations" in {
      client.execute {
        search in "analyzer/test" query termQuery("ngram" -> "cr")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("ngram" -> "craf")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "custom NGramTokenizer" - {
    "should index specified combinations" in {
      client.execute {
        search in "analyzer/test" query matchQuery("custom_ngram" -> "dy")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("custom_ngram" -> "dc50")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "custom EdgeNGram Tokenizer" - {
    "should support side option" in {
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "es")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "nes")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "ones")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "rones")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "hrones")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "thrones")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "ga")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "SnowballAnalyzer" - {
    "should stem words" in {
      client.execute {
        search in "analyzer/test" query termQuery("snowball" -> "sky")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "StandardAnalyzer" - {
    "should honour max token length" in {
      client.execute {
        search in "analyzer/test" query termQuery("standard1" -> "aaaaaaaaaaa")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "PatternAnalyzer" - {
    "should split on regex special character" in {
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "abc")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "def")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "123")
      }.await.getHits.getTotalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "abc123def")
      }.await.getHits.getTotalHits shouldBe 0
    }
    "should split on normal character" in {
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "coldplay")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "jethro tull")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "jethro")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "StopAnalyzer" - {
    "should exclude stop words" in {
      client.execute {
        search in "analyzer/test" query termQuery("stop" -> "and")
      }.await.getHits.getTotalHits shouldBe 0
    }
  }

  "SimpleAnalyzer" - {
    "should split on non-letter" in {
      client.execute {
        search in "analyzer/test" query termQuery("simple" -> "lower")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "WhitespaceAnalyzer" - {
    "should include stop words" in {
      client.execute {
        search in "analyzer/test" query termQuery("whitespace" -> "and")
      }.await.getHits.getTotalHits shouldBe 1
    }
    "should split on whitespace" in {
      client.execute {
        search in "analyzer/test" query termQuery("whitespace" -> "uiop")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }
}
