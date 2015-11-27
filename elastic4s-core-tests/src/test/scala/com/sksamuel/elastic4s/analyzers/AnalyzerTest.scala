package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{FreeSpec, Matchers}

class AnalyzerTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    create index "analyzer" mappings {
      "test" as(
        "keyword" typed StringType analyzer KeywordAnalyzer,
        "snowball" typed StringType analyzer SnowballAnalyzer,
        "whitespace" typed StringType analyzer WhitespaceAnalyzer,
        "stop" typed StringType analyzer StopAnalyzer,
        "apos" typed StringType analyzer CustomAnalyzer("apos"),
        "stop_path" typed StringType analyzer CustomAnalyzer("stop_path"),
        "standard1" typed StringType analyzer CustomAnalyzer("standard1"),
        "simple1" typed StringType analyzer SimpleAnalyzer,
        "pattern1" typed StringType analyzer CustomAnalyzer("pattern1"),
        "pattern2" typed StringType analyzer CustomAnalyzer("pattern2"),
        "ngram" typed StringType analyzer CustomAnalyzer("default_ngram"),
        "edgengram" withType StringType analyzer CustomAnalyzer("edgengram"),
        "custom_ngram" typed StringType analyzer CustomAnalyzer("my_ngram") searchAnalyzer KeywordAnalyzer,
        "shingle" typed StringType analyzer CustomAnalyzer("shingle"),
        "shingle2" typed StringType analyzer CustomAnalyzer("shingle2"),
        "noshingle" typed StringType analyzer CustomAnalyzer("shingle3"),
        "shingleseparator" typed StringType analyzer CustomAnalyzer("shingle4")
        )
    } analysis(
      PatternAnalyzerDefinition("pattern1", "\\d", lowercase = false),
      PatternAnalyzerDefinition("pattern2", ",", lowercase = false),
      CustomAnalyzerDefinition("default_ngram", NGramTokenizer),
      CustomAnalyzerDefinition("my_ngram",
        StandardTokenizer,
        LowercaseTokenFilter,
        ngramTokenFilter("my_ngram_filter") minGram 2 maxGram 5),
      CustomAnalyzerDefinition("edgengram",
        StandardTokenizer,
        LowercaseTokenFilter,
        edgeNGramTokenFilter("edgengram_filter") minGram 2 maxGram 6 side "back"),
      CustomAnalyzerDefinition("standard1", StandardTokenizer("stokenizer1", 10)),
      CustomAnalyzerDefinition(
        "shingle",
        WhitespaceTokenizer,
        LowercaseTokenFilter,
        shingleTokenFilter("filter_shingle") maxShingleSize 3 outputUnigrams false
      ),
      CustomAnalyzerDefinition(
        "shingle2",
        WhitespaceTokenizer,
        LowercaseTokenFilter,
        shingleTokenFilter("filter_shingle2") maxShingleSize 2
      ),
      CustomAnalyzerDefinition(
        "shingle3",
        WhitespaceTokenizer,
        LowercaseTokenFilter,
        shingleTokenFilter("filter_shingle3") outputUnigramsIfNoShingles true
      ),
      CustomAnalyzerDefinition(
        "shingle4",
        WhitespaceTokenizer,
        LowercaseTokenFilter,
        shingleTokenFilter("filter_shingle4") tokenSeperator "#"
      ),
      CustomAnalyzerDefinition(
        "stop_path",
        WhitespaceTokenizer,
        StopTokenFilterPath("new_stop", "stoplist.txt")
      ),
      CustomAnalyzerDefinition(
        "apos",
        WhitespaceTokenizer,
        ApostropheTokenFilter
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
      "stop" -> "and and and red sox",
      "stop_path" -> "testing mics and which",
      "apos" -> "oh no you didn't",
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
      }.await.totalHits shouldBe 0
    }
  }

  "default NGramTokenizer" - {
    "should index 2 combinations" in {
      client.execute {
        search in "analyzer/test" query termQuery("ngram" -> "cr")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("ngram" -> "craf")
      }.await.totalHits shouldBe 0
    }
  }

  "custom NGramTokenizer" - {
    "should index specified combinations" in {
      client.execute {
        search in "analyzer/test" query matchQuery("custom_ngram" -> "dy")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer" / "test" query matchQuery("custom_ngram" -> "dc50")
      }.await.totalHits shouldBe 1
    }
  }

  "custom EdgeNGram Tokenizer" - {
    "should support side option" in {
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "es")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "nes")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "ones")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "rones")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "hrones")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "thrones")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query matchQuery("edgengram" -> "ga")
      }.await.totalHits shouldBe 0
    }
  }

  "SnowballAnalyzer" - {
    "should stem words" in {
      client.execute {
        search in "analyzer/test" query termQuery("snowball" -> "sky")
      }.await.totalHits shouldBe 1
    }
  }

  "StandardAnalyzer" - {
    "should honour max token length" in {
      client.execute {
        search in "analyzer/test" query termQuery("standard1" -> "aaaaaaaaaaa")
      }.await.totalHits shouldBe 0
    }
  }

  "PatternAnalyzer" - {
    "should split on regex special character" in {
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "abc")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "def")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "123")
      }.await.totalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("pattern1" -> "abc123def")
      }.await.totalHits shouldBe 0
    }
    "should split on normal character" in {
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "coldplay")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "jethro tull")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("pattern2" -> "jethro")
      }.await.totalHits shouldBe 0
    }
  }

  "StopAnalyzer" - {
    "should- exclude stop words" in {
      client.execute {
        search in "analyzer/test" query termQuery("stop" -> "and")
      }.await.totalHits shouldBe 0
    }
  }

  "StopAnalyzerPath" - {
    "should exclude stop words from config/stoplist.txt" in {
      client.execute {
        search in "analyzer/test" query termQuery("stop_path" -> "and")
      }.await.totalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("stop_path" -> "testing") // not in stoplist
      }.await.totalHits shouldBe 1
    }
  }

  "SimpleAnalyzer" - {
    "should split on non-letter" in {
      client.execute {
        search in "analyzer/test" query termQuery("simple" -> "lower")
      }.await.totalHits shouldBe 1
    }
  }

  "WhitespaceAnalyzer" - {
    "should include stop words" in {
      client.execute {
        search in "analyzer/test" query termQuery("whitespace" -> "and")
      }.await.totalHits shouldBe 1
    }
    "should split on whitespace" in {
      client.execute {
        search in "analyzer/test" query termQuery("whitespace" -> "uiop")
      }.await.totalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(max_shingle_size = 3, output_unigrams = false)" - {
    "should split on shingle size from 2 to 3 term" in {
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please")
      }.await.totalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide this into")
      }.await.totalHits shouldBe 0
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "please divide this")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "this sentence into")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle" -> "sentence into")
      }.await.totalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(max_shingle_size = 2, output_unigrams = true)" - {
    "should split on shingle size from 1 to 2 term " in {
      client.execute {
        search in "analyzer/test" query termQuery("shingle2" -> "keep")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("shingle2" -> "keep unigram")
      }.await.totalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(output_unigrams_if_no_shingles = true)" - {
    "should keep one term field" in {
      client.execute {
        search in "analyzer/test" query termQuery("noshingle" -> "keep")
      }.await.totalHits shouldBe 1
    }
  }

  "ShingleTokenFilter(token_separator = '#')" - {
    "should use '#' in 'one two' to define shingle term as 'one#two' " in {
      client.execute {
        search in "analyzer/test" query termQuery("shingleseparator" -> "one#two")
      }.await.totalHits shouldBe 1
    }
  }

  "ApostropheCharFilter" - {
    "should remove the apostrophe and the characters after it" in {
      client.execute {
        search in "analyzer/test" query termQuery("apos" -> "didn")
      }.await.totalHits shouldBe 1
      client.execute {
        search in "analyzer/test" query termQuery("apos" -> "didn't")
      }.await.totalHits shouldBe 0
    }
  }
}
