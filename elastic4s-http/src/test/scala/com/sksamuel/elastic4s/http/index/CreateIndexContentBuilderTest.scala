package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.indexes.{CreateIndexDefinition, IndexAliasDefinition}
import com.sksamuel.elastic4s.searches.queries.PrefixQueryDefinition
import org.scalatest.{FunSuite, Matchers}

class CreateIndexContentBuilderTest extends FunSuite with Matchers {

  test("create index should include aliases when set") {
    val create = CreateIndexDefinition("myindex", aliases = Set(IndexAliasDefinition("alias1", None), IndexAliasDefinition("alias2", Option(PrefixQueryDefinition("myfield", "pre")))))
    CreateIndexContentBuilder(create).string shouldBe
      """{"aliases":{"alias1":{},"alias2":{"filter":{"prefix":{"myfield":{"value":"pre"}}}}}}"""
  }
}
