package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.analyzers.{ArmenianLanguageAnalyzer, EnglishLanguageAnalyzer}
import org.scalatest.{FlatSpec, Matchers}

class SearchAsYouTypeFieldTest extends FlatSpec with Matchers with ElasticApi {

  "search as you type def" should "support additional text properties" in {
    val field = searchAsYouType("myfield")
      .fielddata(true)
      .stored(true)
      .index(true)
      .indexOptions("freqs")
      .norms(true)
      .normalizer("mynorm")
      .analyzer(ArmenianLanguageAnalyzer)
      .copyTo("copy1", "copy2")
      .boost(1.2)
      .searchAnalyzer(EnglishLanguageAnalyzer)
      .includeInAll(false)
      .docValues(true)
      .maxInputLength(12)
      .ignoreAbove(30)
      .similarity("classic")
      .nullable(false)
      .nullValue("nully")
      .maxShingleSize(4)
    FieldBuilderFn(field).string() shouldBe
      """{"type":"text","analyzer":"armenian","boost":1.2,"copy_to":["copy1","copy2"],"doc_values":true,"index":"true","normalizer":"mynorm","norms":true,"null_value":"nully","search_analyzer":"english","store":true,"fielddata":true,"max_input_length":12,"ignore_above":30,"similarity":"classic","index_options":"freqs","max_shingle_size":4}"""
  }
}
