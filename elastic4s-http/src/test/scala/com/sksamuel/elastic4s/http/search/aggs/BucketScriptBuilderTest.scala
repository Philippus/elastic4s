package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.BucketScriptDefinition
import org.scalatest.{FunSuite, Matchers}

class BucketScriptBuilderTest extends FunSuite with Matchers {
  val script: ScriptDefinition = ScriptDefinition("params._value0 / params._value1 * 100")
  val bucketsPaths: Seq[String] = Seq("t-shirts>sales", "total_sales")
  val scriptAgg: BucketScriptDefinition =
    BucketScriptDefinition("t-shirt-percentage", script, bucketsPaths).metadata(Map("app_version" -> "7.1"))

  test("BucketScript aggregation should generate expected json") {
    BucketScriptBuilder(scriptAgg).string() shouldBe
      """
        |{"bucket_script":{"buckets_path":{"_value0":"t-shirts>sales","_value1":"total_sales"},
        |"script":{"inline":"params._value0 / params._value1 * 100"}},"meta":{"app_version":"7.1"}}
      |""".stripMargin.replaceAll("\n", "")
  }
}
