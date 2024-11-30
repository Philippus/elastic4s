package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.handlers.fields.SearchAsYouTypeFieldBuilderFn
import com.sksamuel.elastic4s.jackson.JacksonSupport
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SearchAsYouTypeFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "search as you type def" should "support additional text properties" in {
    val field = searchAsYouType("myfield")
      .analyzer("foo")
      .searchAnalyzer("bar")
      .fielddata(true)
      .stored(true)
      .index(true)
      .indexOptions("freqs")
      .norms(true)
      .copyTo("copy1", "copy2")
      .boost(1.2)
      .ignoreAbove(30)
      .similarity("classic")
      .maxShingleSize(4)

    val expectedJson =
      """{"type":"search_as_you_type","analyzer":"foo","search_analyzer":"bar","boost":1.2,"copy_to":["copy1","copy2"],"index":true,"norms":true,"store":true,"fielddata":true,"ignore_above":30,"index_options":"freqs","similarity":"classic","max_shingle_size":4}"""
    SearchAsYouTypeFieldBuilderFn.build(field).string shouldBe expectedJson
    SearchAsYouTypeFieldBuilderFn.toField(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](expectedJson)
    ) shouldBe (field)

  }
}
