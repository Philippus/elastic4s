package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.search.HighlightFieldBuilderFn
import com.sksamuel.elastic4s.searches.HighlightFieldDefinition
import org.scalatest.{FunSuite, Matchers}
import com.sksamuel.elastic4s.http.ElasticDsl._

class HighlightFieldBuilderFnTest extends FunSuite with Matchers {

  test("'boundaryChars' generates 'boundary_chars' field.") {
    val highlight = HighlightFieldDefinition("text").boundaryChars("test")
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"boundary_chars":"test"}}"""
  }
  test("'boundaryMaxScan' generates 'boundary_max_scan' field.") {
    val highlight = HighlightFieldDefinition("text").boundaryMaxScan(20)
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"boundary_max_scan":20}}"""
  }
  test("'forceSource' generates 'force_source' field.") {
    val highlight = HighlightFieldDefinition("text").forceSource(true)
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"force_source":true}}"""
  }
  test("'fragmentOffset' generates 'fragment_offset' field.") {
    val highlight = HighlightFieldDefinition("text").fragmentOffset(100)
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"fragment_offset":100}}"""
  }
  test("'fragmentSize' generates 'fragment_size' field.") {
    val highlight = HighlightFieldDefinition("text").fragmentSize(108)
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"fragment_size":108}}"""
  }
  test("'query' generates 'highlight_query' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .query(matchQuery("body", "foo"))
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"highlight_query":{"match":{"body":{"query":"foo"}}}}}"""
  }
  test("'matchedFields' generates proper 'matched_fields' field as array field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .matchedFields("text", "text.ngram", "text.japanese")
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"matched_fields":["text","text.ngram","text.japanese"]}}"""
  }
  test("'highlighterType' generates 'type' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .highlighterType("fvh")
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"type":"fvh"}}"""
  }
  test("'noMatchSize' generates 'no_match_size' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .noMatchSize(33)
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"no_match_size":33}}"""
  }
  test("'numberOfFragments' generates 'number_of_fragments' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .numberOfFragments(12)
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"number_of_fragments":12}}"""
  }
  test("'order' generates 'order' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .order("asc")
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"order":"asc"}}"""
  }
  test("'phraseLimit' generates 'phrase_limit' field.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .phraseLimit(99)
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"phrase_limit":99}}"""
  }
  // Post & Pre tags
  test("not generates post & pre tags if they are not specified.") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .preTag(Nil)
        .postTag(Nil)
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{}}"""
  }
  test("em tag will be generated as post tag if specified pre tag only") {
    // Given
    val highlight =
      HighlightFieldDefinition("text")
        .preTag("<p>", "<b>")
        .postTag(Nil)
    // Then
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"post_tags":["</em>"],"pre_tags":["<p>","<b>"]}}"""
  }
  test("em tag will be generated as pre tag if specified post tag only") {
    val highlight = HighlightFieldDefinition("text").preTag(Nil).postTag("<p>", "<b>")
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"post_tags":["<p>","<b>"],"pre_tags":["<em>"]}}"""
  }
  test("specified post & pre tags will be generated") {
    val highlight = HighlightFieldDefinition("text").preTag("<a>", "<b>").postTag("<c>", "<d>")
    HighlightFieldBuilderFn(highlight).string() shouldBe
      """{"text":{"post_tags":["<c>","<d>"],"pre_tags":["<a>","<b>"]}}"""
  }
  test("'requiredFieldMatch' generates 'require_field_match' field.") {
    val highlight = HighlightFieldDefinition("text").requireFieldMatch(false)
    HighlightFieldBuilderFn(highlight).string() shouldBe """{"text":{"require_field_match":false}}"""
  }
}
