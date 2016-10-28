package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.WordSpec

class AnalyzerDslTest extends WordSpec with ElasticDsl2 {

  "analyzer dsl" should {
    "allow snowball token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        snowball tokenfilter "snowy" lang "english"
      )
    }
    "allow common grams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        commonGrams tokenfilter "snowy" commonWords Seq("hello", "world") queryMode true ignoreCase false
      )
    }
    "allow ngrams grams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        ngram tokenfilter "snowy" maxGram 2 minGram 3
      )
    }
    "allow edge ngrams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        edgeNGram tokenfilter "edgy" maxGram 2 minGram 3 side "back"
      )
    }
    "allow lowercasecase token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        stemmer tokenfilter "stemmy" lang "German"
      )
    }
  }
}
