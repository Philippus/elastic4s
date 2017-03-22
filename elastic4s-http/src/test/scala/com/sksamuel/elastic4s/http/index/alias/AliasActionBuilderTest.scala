package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{RemoveAliasActionDefinition, AddAliasActionDefinition, IndicesAliasesRequestDefinition}
import com.sksamuel.elastic4s.searches.queries.matches.MatchAllQueryDefinition
import org.scalatest.{FunSuite, Matchers}

class AliasActionBuilderTest extends FunSuite with Matchers {

  test("alias action should generate expected json") {
    val actions = IndicesAliasesRequestDefinition(Seq(
      AddAliasActionDefinition("alias1", "test"),
      RemoveAliasActionDefinition("alias1", "test1"),
      AddAliasActionDefinition("alias2", "test2", filter = Some(MatchAllQueryDefinition()))
    ))

    AliasActionBuilder(actions).string() shouldBe
      """{"actions":[{"add":{"index":"test","alias":"alias1"}},{"remove":{"index":"test1","alias":"alias1"}},{"add":{"index":"test2","alias":"alias2","filter":{"match_all":{}}}}]}"""
  }

}
