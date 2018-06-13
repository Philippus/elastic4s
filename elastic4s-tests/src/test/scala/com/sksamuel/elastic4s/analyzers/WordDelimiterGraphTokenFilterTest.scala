package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class WordDelimiterGraphTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "WordDelimiterGraphTokenFilter builder" should {
    "set generateNumberParts" in {
      wordDelimiterGraphTokenFilter("testy")
        .generateNumberParts(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","generate_number_parts":true}"""
    }
    "set generateWordParts" in {
      wordDelimiterGraphTokenFilter("testy")
        .generateWordParts(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","generate_word_parts":true}"""
    }
    "set splitOnCaseChange" in {
      wordDelimiterGraphTokenFilter("testy")
        .splitOnCaseChange(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","split_on_case_change":true}"""
    }
    "set splitOnNumerics" in {
      wordDelimiterGraphTokenFilter("testy")
        .splitOnNumerics(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","split_on_numerics":true}"""
    }
    "set stemEnglishPossesive" in {
      wordDelimiterGraphTokenFilter("testy")
        .stemEnglishPossesive(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","stem_english_possessive":true}"""
    }
    "set catenateAll" in {
      wordDelimiterGraphTokenFilter("testy")
        .catenateAll(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","catenate_all":true}"""
    }
    "set catenateNumbers" in {
      wordDelimiterGraphTokenFilter("testy")
        .catenateNumbers(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","catenate_numbers":true}"""
    }
    "set catenateWords" in {
      wordDelimiterGraphTokenFilter("testy")
        .catenateWords(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","catenate_words":true}"""
    }
    "set preserveOriginal" in {
      wordDelimiterGraphTokenFilter("testy")
        .preserveOriginal(true)
        .json
        .string shouldBe """{"type":"word_delimiter_graph","preserve_original":true}"""
    }
  }
}
