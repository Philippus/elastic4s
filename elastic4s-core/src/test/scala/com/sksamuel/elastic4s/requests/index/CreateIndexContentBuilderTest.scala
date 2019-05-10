package com.sksamuel.elastic4s.requests.index

import com.sksamuel.elastic4s.requests.analysis.{Analysis, CustomNormalizer, PatternReplaceCharFilter, TruncateTokenFilter, UniqueTokenFilter}
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexContentBuilder, CreateIndexRequest, IndexAliasRequest}
import com.sksamuel.elastic4s.requests.searches.queries.PrefixQuery
import org.scalatest.{FunSuite, Matchers}

class CreateIndexContentBuilderTest extends FunSuite with Matchers {

  test("create index should include aliases when set") {
    val create = CreateIndexRequest("myindex", aliases = Set(IndexAliasRequest("alias1", None), IndexAliasRequest("alias2", Option(PrefixQuery("myfield", "pre")))))
    CreateIndexContentBuilder(create).string shouldBe
      """{"aliases":{"alias1":{},"alias2":{"filter":{"prefix":{"myfield":{"value":"pre"}}}}}}"""
  }

  test("with analysis") {

    val analysis = Analysis(
      Nil,
      normalizers = List(
        CustomNormalizer("my_normalizer", List("my_pattern_replace"), List("my_unique_filter", "my_truncate_filter"))
      ),
      tokenFilters = List(
        TruncateTokenFilter("my_truncate_filter", 123),
        UniqueTokenFilter("my_unique_filter", true)
      ),
      charFilters = List(
        PatternReplaceCharFilter(
          "my_pattern_replace",
          "qwe",
          "ert"
        )
      )
    )

    val create = CreateIndexRequest("myindex").analysis(analysis)
    CreateIndexContentBuilder(create).string shouldBe """{"settings":{"analysis":{"normalizer":{"my_normalizer":{"type":"custom","filter":["my_unique_filter","my_truncate_filter"],"char_filter":["my_pattern_replace"]}},"char_filter":{"my_pattern_replace":{"type":"pattern_replace","pattern":"qwe","replacement":"ert"}},"filter":{"my_truncate_filter":{"type":"truncate","length":123},"my_unique_filter":{"type":"unique","only_on_same_position":true}}}}}"""
  }
}
