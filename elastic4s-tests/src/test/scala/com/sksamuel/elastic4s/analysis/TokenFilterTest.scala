package com.sksamuel.elastic4s.analysis

import com.sksamuel.elastic4s.JsonSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TokenFilterTest extends AnyWordSpec with Matchers with JsonSugar {
  "WordDelimiterGraphTokenFilter" should {
    "build json" in {
      WordDelimiterGraphTokenFilter(
        name = "my_word_delimiter_graph",
        preserveOriginal = Option(true),
        catenateNumbers = Option(true),
        catenateWords = Option(true),
        catenateAll = Option(true),
        generateWordParts = Option(true),
        generateNumberParts = Option(true),
        protectedWordsPath = Option("my_protected_words.txt"),
        splitOnCaseChange = Option(true),
        splitOnNumerics = Option(true),
        stem_english_possessive = Option(true),
        typeTablePath = Option("my_table_path.txt")
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/worddelimitergraphtokenfilter_raw.json")
    }
  }

  "WordDelimiterTokenFilter" should {
    "build json" in {
      WordDelimiterTokenFilter(
        name = "my_word_delimiter",
        preserveOriginal = Option(true),
        catenateNumbers = Option(true),
        catenateWords = Option(true),
        catenateAll = Option(true),
        generateWordParts = Option(true),
        generateNumberParts = Option(true),
        protectedWordsPath = Option("my_protected_words.txt"),
        splitOnCaseChange = Option(true),
        splitOnNumerics = Option(true),
        stemEnglishPossesive = Option(true),
        typeTablePath = Option("my_table_path.txt")
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/worddelimitertokenfilter_raw.json")
    }
  }
}
