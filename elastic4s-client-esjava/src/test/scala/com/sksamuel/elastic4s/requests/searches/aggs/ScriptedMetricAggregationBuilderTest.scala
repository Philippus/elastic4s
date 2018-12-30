package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.ScriptedMetricAggregation
import org.scalatest.{FunSuite, Matchers}

class ScriptedMetricAggregationBuilderTest extends FunSuite with Matchers {
  test("scripted metric aggregation should generate expected json") {
    val q = ScriptedMetricAggregation("scripted_metric")
        .initScript("params._agg.transactions = []")
        .mapScript("params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)")
        .combineScript("double profit = 0; for (t in params._agg.transactions) { profit += t } return profit")
        .reduceScript("double profit = 0; for (a in params._aggs) { profit += a } return profit")
    ScriptedMetricAggregationBuilder(q).string() shouldBe
      """{"scripted_metric":{"init_script":"params._agg.transactions = []","map_script":"params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)","combine_script":"double profit = 0; for (t in params._agg.transactions) { profit += t } return profit","reduce_script":"double profit = 0; for (a in params._aggs) { profit += a } return profit"}}"""
  }
}
