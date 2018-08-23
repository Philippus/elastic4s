package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.ScriptedMetricAggregationDefinition
import org.scalatest.{FunSuite, Matchers}

class ScriptedMetricAggregationBuilderTest extends FunSuite with Matchers {
  val initScript: ScriptDefinition = ScriptDefinition("params._agg.transactions = []")
  val mapScript: ScriptDefinition = ScriptDefinition("params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)")
  val combineScript: ScriptDefinition = ScriptDefinition("double profit = 0; for (t in params._agg.transactions) { profit += t } return profit")
  val reduceScript: ScriptDefinition = ScriptDefinition("double profit = 0; for (a in params._aggs) { profit += a } return profit")
  val scriptAgg: ScriptedMetricAggregationDefinition =
    ScriptedMetricAggregationDefinition("profit_script", Some(initScript), Some(mapScript), Some(combineScript), Some(reduceScript)).metadata(Map("app_version" -> "7.1"))

  test("ScriptedMetric aggregation with default params should generate expected json") {
    ScriptedMetricAggregationBuilder(scriptAgg).string() shouldBe
      """
        |{"scripted_metric":{"init_script":{"inline":"params._agg.transactions = []"},
        |"map_script":{"inline":"params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)"},
        |"combine_script":{"inline":"double profit = 0; for (t in params._agg.transactions) { profit += t } return profit"},
        |"reduce_script":{"inline":"double profit = 0; for (a in params._aggs) { profit += a } return profit"}},
        |"meta":{"app_version":"7.1"}}
      |""".stripMargin.replaceAll("\n", "")
  }

  test("ScriptedMetric aggregation with provided params should generate expected json") {
    val agg = scriptAgg
      .params(Map("currency_rate" -> Double.box(2.1)))
      .mapScript("params._agg.transactions.add(doc.type.value == 'sale' ? params.currency_rate * doc.amount.value : -1 * params.currency_rate * doc.amount.value)")
    ScriptedMetricAggregationBuilder(agg).string() shouldBe
      """
        |{"scripted_metric":{"params":{"_agg":{},"currency_rate":2.1},
        |"init_script":{"inline":"params._agg.transactions = []"},
        |"map_script":{"inline":"params._agg.transactions.add(doc.type.value == 'sale' ? params.currency_rate * doc.amount.value : -1 * params.currency_rate * doc.amount.value)"},
        |"combine_script":{"inline":"double profit = 0; for (t in params._agg.transactions) { profit += t } return profit"},
        |"reduce_script":{"inline":"double profit = 0; for (a in params._aggs) { profit += a } return profit"}},
        |"meta":{"app_version":"7.1"}}
      |""".stripMargin.replaceAll("\n", "")
  }
}
