package com.sksamuel.elastic4s

import org.scalatest.WordSpec

class AnalyzerDslTest extends WordSpec with ElasticDsl {

  "analyzer dsl" should {
    "allow snowball token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        snowball token filter name "snowy" lang "english"
      )
    }
    "allow common grams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        commonGrams token filter name "snowy" commonWords Seq("hello", "world") queryMode true ignoreCase false
      )
    }
    "allow ngrams grams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        ngram token filter name "snowy" maxGram 2 minGram 3
      )
    }
    "allow edge ngrams token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        edgeNGram token filter name "edgy" maxGram 2 minGram 3 side "back"
      )
    }
    "allow lowercasecase token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        lowercase token filter
      )
    }
    "allow ascii folding token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        asciiFolding token filter
      )
    }
    "allow stemmer token filter" in {
      CustomAnalyzerDefinition(
        "name",
        WhitespaceTokenizer,
        stemmer token filter name "stemmy" lang "German"
      )
    }
  }
}
