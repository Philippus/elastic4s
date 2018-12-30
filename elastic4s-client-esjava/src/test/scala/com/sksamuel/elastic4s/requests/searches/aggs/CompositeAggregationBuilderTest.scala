package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.{SearchBodyBuilderFn, SearchRequest}
import org.scalatest.{FunSuite, Matchers}

class CompositeAggregationBuilderTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("CompositeAggregationBuilder should build simple terms-valued composites") {
    val search = SearchRequest("myindex" / "mytype").aggs(
      CompositeAggregation("comp", sources = Seq(
        TermsValueSource("s1", field = Some("f1")))
      )
    )

    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"comp":{"composite":{"sources":[{"s1":{"terms":{"field":"f1"}}}]}}}}"""
  }

  test("CompositeAggregationBuilder should terms-valued composites with multiple terms") {
    val search = SearchRequest("myindex" / "mytype").aggs(
      CompositeAggregation("comp", sources = Seq(
        TermsValueSource("s1", field = Some("f1")),
        TermsValueSource("s2", field = Some("f2"))
      ))
    )

    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"comp":{"composite":{"sources":[{"s1":{"terms":{"field":"f1"}}},{"s2":{"terms":{"field":"f2"}}}]}}}}"""
  }

  test("CompositeAggregationBuilder should build script-valued composites") {
    val search = SearchRequest("myindex" / "mytype").aggs(
      CompositeAggregation("comp", sources = Seq(
        TermsValueSource("s1", script = Some(Script("doc['product'].value"))))
      )
    )

    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"comp":{"composite":{"sources":[{"s1":{"terms":{"script":{"source":"doc['product'].value"}}}}]}}}}"""
  }

  test("CompositeAggregationBuilder should respect all possible value types and attributes") {
    val search = SearchRequest("myindex" / "mytype").aggs(
      CompositeAggregation("comp", sources = Seq(
        TermsValueSource("s1", field = Some("f1"), order = Some("desc")),
        HistogramValueSource("s2", 5, field = Some("f2"), order = Some("desc")),
        DateHistogramValueSource("s3", "5d", field = Some("f3"), order = Some("desc"), timeZone = Some("+01:00"))
      ))
    )

    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"comp":{"composite":{"sources":[{"s1":{"terms":{"field":"f1","order":"desc"}}},{"s2":{"histogram":{"field":"f2","order":"desc","interval":5}}},{"s3":{"date_histogram":{"field":"f3","order":"desc","interval":"5d","time_zone":"+01:00"}}}]}}}}"""

  }

}
