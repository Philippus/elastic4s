//package com.sksamuel.elastic4s.analyzers
//
//import java.io.PrintWriter
//
//import com.sksamuel.elastic4s.http.ElasticDsl
//import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
//import org.scalatest.{FreeSpec, Matchers}
//
//import scala.util.Try
//
//class AnalyzerTest extends FreeSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {
//
//  // setup the stop file list
//  node.pathConfig.toFile.mkdirs()
//  private val newStopListFile = (node.pathConfig resolve "stoplist.txt").toFile
//  private val writer = new PrintWriter(newStopListFile)
//  writer.write("a\nan\nthe\nis\nand\nwhich") // writing the stop words to the file
//  writer.close()
//
//  Try {
//    http.execute {
//      ElasticDsl.deleteIndex("analyzer")
//    }.await
//  }
//
//  http.execute {
//    createIndex("analyzer").mappings {
//      mapping("test") fields (
//        textField("keyword") analyzer KeywordAnalyzer,
//        textField("snowball") analyzer SnowballAnalyzer,
//        textField("whitespace") analyzer WhitespaceAnalyzer,
//        textField("stop") analyzer StopAnalyzer,
//        textField("apos") analyzer CustomAnalyzer("apos"),
//        textField("stop_path") analyzer CustomAnalyzer("stop_path"),
//        textField("standard1") analyzer CustomAnalyzer("standard1"),
//        textField("simple1") analyzer SimpleAnalyzer,
//        textField("pattern1") analyzer CustomAnalyzer("pattern1"),
//        textField("pattern2") analyzer CustomAnalyzer("pattern2"),
//        textField("ngram") analyzer CustomAnalyzer("default_ngram"),
//        textField("edgengram") analyzer CustomAnalyzer("edgengram"),
//        textField("custom_ngram") analyzer CustomAnalyzer("my_ngram") searchAnalyzer KeywordAnalyzer,
//        textField("shingle") analyzer CustomAnalyzer("shingle"),
//        textField("shingle2") analyzer CustomAnalyzer("shingle2"),
//        textField("noshingle") analyzer CustomAnalyzer("shingle3"),
//        textField("shingleseparator") analyzer CustomAnalyzer("shingle4")
//        )
//    } analysis(
//      PatternAnalyzerDefinition("pattern1", "\\d", lowercase = false),
//      PatternAnalyzerDefinition("pattern2", ",", lowercase = false),
//      customAnalyzer("default_ngram", NGramTokenizer),
//      customAnalyzer("my_ngram",
//        StandardTokenizer,
//        LowercaseTokenFilter,
//        ngramTokenFilter("my_ngram_filter") minGram 2 maxGram 5),
//      customAnalyzer("edgengram",
//        StandardTokenizer,
//        LowercaseTokenFilter,
//        edgeNGramTokenFilter("edgengram_filter") minGram 2 maxGram 6 side "back"),
//      customAnalyzer("standard1", StandardTokenizer("stokenizer1", 10)),
//      customAnalyzer(
//        "shingle",
//        WhitespaceTokenizer,
//        LowercaseTokenFilter,
//        shingleTokenFilter("filter_shingle") maxShingleSize 3 outputUnigrams false
//      ),
//      customAnalyzer(
//        "shingle2",
//        WhitespaceTokenizer,
//        LowercaseTokenFilter,
//        shingleTokenFilter("filter_shingle2") maxShingleSize 2
//      ),
//      customAnalyzer(
//        "shingle3",
//        WhitespaceTokenizer,
//        LowercaseTokenFilter,
//        shingleTokenFilter("filter_shingle3") outputUnigramsIfNoShingles true
//      ),
//      customAnalyzer(
//        "shingle4",
//        WhitespaceTokenizer,
//        LowercaseTokenFilter,
//        shingleTokenFilter("filter_shingle4") tokenSeperator "#"
//      ),
//      customAnalyzer(
//        "stop_path",
//        WhitespaceTokenizer,
//        StopTokenFilterPath("new_stop", "stoplist.txt")
//      ),
//      customAnalyzer(
//        "apos",
//        WhitespaceTokenizer,
//        ApostropheTokenFilter
//      )
//      )
//  }.await
//
//  http.execute {
//    indexInto("analyzer" / "test") fields(
//      "keyword" -> "light as a feather",
//      "snowball" -> "flying in the skies",
//      "whitespace" -> "and and and qwerty uiop",
//      "standard1" -> "aaaaaaaaaaa",
//      "simple" -> "LOWER-CASED",
//      "ngram" -> "starcraft",
//      "custom_ngram" -> "dyson dc50i",
//      "edgengram" -> "gameofthrones",
//      "stop" -> "and and and red sox",
//      "stop_path" -> "testing mics and which",
//      "apos" -> "oh no you didn't",
//      "pattern1" -> "abc123def",
//      "pattern2" -> "jethro tull,coldplay",
//      "shingle" -> "please divide this sentence into shingles",
//      "shingle2" -> "keep unigram",
//      "noshingle" -> "keep",
//      "shingleseparator" -> "one two"
//      )
//  }.await
//
//  refresh("analyzer")
//  blockUntilCount(1, "analyzer")
//
//  "KeywordAnalyzer" - {
//    "should index entire string as a single token" in {
//      http.execute {
//        search("analyzer" / "test") query termQuery("keyword" -> "feather")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "default NGramTokenizer" - {
//    "should index 2 combinations" in {
//      http.execute {
//        search("analyzer/test") query termQuery("ngram" -> "cr")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("ngram" -> "craf")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "custom NGramTokenizer" - {
//    "should index specified combinations" in {
//      http.execute {
//        search("analyzer/test") query matchQuery("custom_ngram" , "dy")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer" / "test") query matchQuery("custom_ngram" , "dc50")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "custom EdgeNGram Tokenizer" - {
//    "should support side option" in {
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" , "es")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" , "nes")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" , "ones")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" , "rones")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" , "hrones")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" -> "thrones")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query matchQuery("edgengram" -> "ga")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "SnowballAnalyzer" - {
//    "should stem words" in {
//      http.execute {
//        search("analyzer/test").query(termQuery("snowball" -> "sky"))
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "StandardAnalyzer" - {
//    "should honour max token length" in {
//      http.execute {
//        search("analyzer/test") query termQuery("standard1" -> "aaaaaaaaaaa")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "PatternAnalyzer" - {
//    "should split on regex special character" in {
//      http.execute {
//        search("analyzer/test") query termQuery("pattern1" -> "abc")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("pattern1" -> "def")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("pattern1" -> "123")
//      }.await.totalHits shouldBe 0
//      http.execute {
//        search("analyzer/test") query termQuery("pattern1" -> "abc123def")
//      }.await.totalHits shouldBe 0
//    }
//    "should split on normal character" in {
//      http.execute {
//        search("analyzer/test") query termQuery("pattern2" -> "coldplay")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("pattern2" -> "jethro tull")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("pattern2" -> "jethro")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "StopAnalyzer" - {
//    "should- exclude stop words" in {
//      http.execute {
//        search in "analyzer/test" query termQuery("stop" -> "and")
//      }.await.totalHits shouldBe 0
//    }
//  }
//
//  "StopAnalyzerPath" - {
//    "should exclude stop words from config/stoplist.txt" in {
//      http.execute {
//        search("analyzer/test") query termQuery("stop_path" -> "and")
//      }.await.totalHits shouldBe 0
//      http.execute {
//        search("analyzer/test") query termQuery("stop_path" -> "testing") // not in stoplist
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "SimpleAnalyzer" - {
//    "should split on non-letter" in {
//      http.execute {
//        search in "analyzer/test" query termQuery("simple" -> "lower")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "WhitespaceAnalyzer" - {
//    "should include stop words" in {
//      http.execute {
//        search in "analyzer/test" query termQuery("whitespace" -> "and")
//      }.await.totalHits shouldBe 1
//    }
//    "should split on whitespace" in {
//      http.execute {
//        search in "analyzer/test" query termQuery("whitespace" -> "uiop")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "ShingleTokenFilter(max_shingle_size = 3, output_unigrams = false)" - {
//    "should split on shingle size from 2 to 3 term" in {
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "please")
//      }.await.totalHits shouldBe 0
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "please divide this into")
//      }.await.totalHits shouldBe 0
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "please divide")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "please divide this")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "this sentence into")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle" -> "sentence into")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "ShingleTokenFilter(max_shingle_size = 2, output_unigrams = true)" - {
//    "should split on shingle size from 1 to 2 term " in {
//      http.execute {
//        search("analyzer/test").query(termQuery("shingle2" -> "keep"))
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search in "analyzer/test" query termQuery("shingle2" -> "keep unigram")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "ShingleTokenFilter(output_unigrams_if_no_shingles = true)" - {
//    "should keep one term field" in {
//      http.execute {
//        search("analyzer/test") query termQuery("noshingle" -> "keep")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "ShingleTokenFilter(token_separator = '#')" - {
//    "should use '#' in 'one two' to define shingle term as 'one#two' " in {
//      http.execute {
//        search("analyzer/test") query termQuery("shingleseparator" -> "one#two")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//  "ApostropheCharFilter" - {
//    "should remove the apostrophe and the characters after it" in {
//      http.execute {
//        search("analyzer/test") query termQuery("apos" -> "didn")
//      }.await.totalHits shouldBe 1
//      http.execute {
//        search("analyzer/test") query termQuery("apos" -> "didn't")
//      }.await.totalHits shouldBe 0
//    }
//  }
//}
