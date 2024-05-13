package com.sksamuel.elastic4s.analysis

import com.sksamuel.elastic4s.JsonSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TokenFilterTest extends AnyWordSpec with Matchers with JsonSugar {
  "SynonymTokenFilter" should {
    "build json with synonyms set" in {
      SynonymTokenFilter(
        name = "my_synonym",
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true),
        synonymsSet = Option("my_synonyms_set")
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymtokenfilter_set_raw.json")
    }
    "build json with path" in {
      SynonymTokenFilter(
        name = "my_synonym",
        path = Option("analysis/synonyms.txt"),
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true)
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymtokenfilter_path_raw.json")
    }
    "build json with synonyms" in {
      SynonymTokenFilter(
        name = "my_synonym",
        synonyms = Set("british,english", "queen,monarch"),
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true)
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymtokenfilter_synonyms_raw.json")
    }
  }

  "SynonymGraphTokenFilter" should {
    "build json with synonyms set" in {
      SynonymGraphTokenFilter(
        name = "my_synonym",
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true),
        synonymsSet = Option("my_synonyms_set")
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymgraphtokenfilter_set_raw.json")
    }
    "build json with path" in {
      SynonymGraphTokenFilter(
        name = "my_synonym",
        path = Option("analysis/synonyms.txt"),
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true)
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymgraphtokenfilter_path_raw.json")
    }
    "build json with synonyms" in {
      SynonymGraphTokenFilter(
        name = "my_synonym",
        synonyms = Set("british,english", "queen,monarch"),
        ignoreCase = Option(true),
        format = Option("solr"),
        expand = Option(true),
        tokenizer = Option("whitespace"),
        updateable = Option(true),
        lenient = Option(true)
      ).build.string should matchJsonResource("/json/analysis/tokenfilter/synonymgraphtokenfilter_synonyms_raw.json")
    }
  }

  "WordDelimiterGraphTokenFilter" should {
    "build json" in {
      WordDelimiterGraphTokenFilter(
        adjustOffsets = Option(true),
        name = "my_word_delimiter_graph",
        preserveOriginal = Option(true),
        catenateNumbers = Option(true),
        catenateWords = Option(true),
        catenateAll = Option(true),
        generateWordParts = Option(true),
        generateNumberParts = Option(true),
        ignoreKeywords = Option(true),
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
