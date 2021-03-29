package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.fields.builders.TextFieldBuilderFn
import com.sksamuel.elastic4s.requests.analyzers.{ArmenianLanguageAnalyzer, EnglishLanguageAnalyzer}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "text field def" should "support text properties" in {
    val field = textField("myfield")
      .fielddata(true)
      .stored(true)
      .index(true)
      .indexOptions("freqs")
      .norms(true)
//      .normalizer("mynorm")
      .analyzer(ArmenianLanguageAnalyzer.name)
      .copyTo("copy1", "copy2")
      .boost(1.2)
      .searchAnalyzer(EnglishLanguageAnalyzer.name)
      .similarity("classic")
    TextFieldBuilderFn.build(field).string() shouldBe
      """{"type":"text","analyzer":"armenian","boost":1.2,"copy_to":["copy1","copy2"],"doc_values":true,"index":"true","normalizer":"mynorm","norms":true,"null_value":"nully","search_analyzer":"english","store":true,"fielddata":true,"max_input_length":12,"similarity":"classic","index_options":"freqs"}"""
  }
}
