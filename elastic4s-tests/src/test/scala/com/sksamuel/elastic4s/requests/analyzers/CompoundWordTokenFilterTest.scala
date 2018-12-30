package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class CompoundWordTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "CompoundWordTokenFilter builder" should {
    "set type" in {
      compoundWordTokenFilter("testy", DictionaryDecompounder).json.string shouldBe """{"type":"dictionary_decompounder"}"""
    }
    "set word list" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).wordList("boo", "foo").json.string shouldBe """{"type":"hyphenation_decompounder","word_list":["boo","foo"]}"""
    }
    "set word list path" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).wordListPath("config/word.txt").json.string shouldBe """{"type":"hyphenation_decompounder","word_list_path":"config/word.txt"}"""
    }
    "set hyphenation patterns path" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).wordListPath("config/hyphens.txt").json.string shouldBe """{"type":"hyphenation_decompounder","word_list_path":"config/hyphens.txt"}"""
    }
    "set min word size" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).minWordSize(7).json.string shouldBe """{"type":"hyphenation_decompounder","min_word_size":7}"""
    }
    "set min subword size" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).minSubwordSize(3).json.string shouldBe """{"type":"hyphenation_decompounder","min_subword_size":3}"""
    }
    "set max subword size" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).maxSubwordSize(18).json.string shouldBe """{"type":"hyphenation_decompounder","max_subword_size":18}"""
    }
    "set only longest match" in {
      compoundWordTokenFilter("testy", HyphenationDecompounder).onlyLongestMatch(true).json.string shouldBe """{"type":"hyphenation_decompounder","only_longest_match":true}"""
    }
  }
}

