package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.{FreeSpec, Matchers}

class AnalyzerTest extends FreeSpec with Matchers with ElasticSugar {
  client.execute {
    create index "analyzer" mappings {
      "test" as(
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
        "custom_ngram" typed StringType indexAnalyzer CustomAnalyzer("my_ngram") searchAnalyzer KeywordAnalyzer,
        "shingle" typed StringType analyzer CustomAnalyzer("shingle"),
        "shingle2" typed StringType analyzer CustomAnalyzer("shingle2"),
        "noshingle" typed StringType analyzer CustomAnalyzer("shingle3"),
        "shingleseparator" typed StringType analyzer CustomAnalyzer("shingle4")
        )
    } analysis(
      PatternAnalyzerDefinition("pattern1", "\\d", false),
      PatternAnalyzerDefinition("pattern2", ",", false),
      CustomAnalyzerDefinition("default_ngram", NGramTokenizer),
      CustomAnalyzerDefinition("my_ngram",
        StandardTokenizer,
        lowercase token filter,
        ngram token filter name "my_ngram_filter" minGram 2 maxGram 5),
      CustomAnalyzerDefinition("edgengram",
        StandardTokenizer,
        lowercase token filter,
        edgeNGram token filter name "edgengram_filter" minGram 2 maxGram 6 side "back"),
      CustomAnalyzerDefinition("standard1", StandardTokenizer("stokenizer1", 10)),
      CustomAnalyzerDefinition(
        "shingle",
        WhitespaceTokenizer,
        lowercase token filter,
        shingle token filter name "filter_shingle" maxShingleSize 3 outputUnigrams true
      ),
      CustomAnalyzerDefinition(
        "shingle2",
        WhitespaceTokenizer,
        lowercase token filter,
        shingle token filter name "filter_shingle2" maxShingleSize 2
      ),
      CustomAnalyzerDefinition(
        "shingle3",
        WhitespaceTokenizer,
        lowercase token filter,
        shingle token filter name "filter_shingle3" outputUnigramsIfNoShingles true
      ),
      CustomAnalyzerDefinition(
        "shingle4",
        WhitespaceTokenizer,
        lowercase token filter,
        shingle token filter name "filter_shingle4" tokenSeperator "#"
      )
      )
  }.await

  client.execute {
    index into "analyzer" / "test" fields(
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
      "pattern2" -> "jethro tull,coldplay",
      "shingle" -> "please divide this sentence into shingles",
      "shingle2" -> "keep unigram",
      "noshingle" -> "keep",
      "shingleseparator" -> "one two"
      )
  }.await

  refresh("analyzer")
  blockUntilCount(1, "analyzer")

  "KeywordAnalyzer" - {
    "should index entire string as a single token" in {
      client.execute {
        search in "analyzer" / "test" query termQuery("keyword" -> "feather")
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
        search in "analyzer" / "test" query matchQuery("custom_ngram" -> "dc50")
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

  "ShingleTokenFilter(max_shingle_size = 3, output_unigrams = false)" - {
    "should split on shingle size from 2 to 3 term" in {
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please")
      }.await.getHits.getTotalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide this into")
      }.await.getHits.getTotalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide this")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "this sentence into")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "sentence into")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(max_shingle_size = 2, output_unigrams = true)" - {
    "should split on shingle size from 1 to 2 term " in {
      client.execute {
        search in "analyzer/test" query termQuery("shingle2" -> "keep")
      }.await.getHits.getTotalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle2" -> "keep unigram")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(output_unigrams_if_no_shingles = true)" - {
    "should keep one term field" in {
      client.execute {
        search in "analyzer/test" query termQuery("noshingle" -> "keep")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(token_separator = '#')" - {
    "should use '#' in 'one two' to define shingle term as 'one#two' " in {
      client.execute {
        search in "analyzer/test" query termQuery("shingleseparator" -> "one#two")
      }.await.getHits.getTotalHits shouldBe 1
    }
  }

}
