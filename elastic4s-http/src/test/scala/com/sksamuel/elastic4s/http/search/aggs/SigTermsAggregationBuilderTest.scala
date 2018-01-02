package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.{SigTermsAggregationDefinition, TopHitsAggregationDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.MutualInformation
import org.scalatest.{FunSuite, Matchers}

/**
  * Project: elastic4s
  * Package: com.sksamuel.elastic4s.http.search.aggs
  * Created by asoloviov on 1/2/18 3:46 PM.
  */
class SigTermsAggregationBuilderTest extends FunSuite with Matchers {
  test("aggregation should generate expected json") {
    val agg = SigTermsAggregationDefinition("significant_crime_types")
      .field("crime_type")
      .backgroundFilter(TermQueryDefinition("city", "Madrid"))
      .significanceHeuristic(new MutualInformation(true, false))
      .minDocCount(5)
      .executionHint("map")
      .includeExclude(".*theft.*", "bicycle.*")
      .shardMinDocCount(10)
      .size(3)
      .shardSize(30)
      .metadata(Map("color" -> "red"))
      .subAggregations(TopHitsAggregationDefinition("recent_significant_crime_type").sortBy(FieldSortDefinition("date").desc()).size(6))
    SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"min_doc_count":5,"execution_hint":"map","size":3,"include":".*theft.*","exclude":"bicycle.*","field":"crime_type","shard_min_doc_count":10,"shard_size":30,"background_filter":{"term":{"city":{"value":"Madrid"}}},"mutual_information":{"include_negatives":true,"background_is_superset":false}},"aggs":{"recent_significant_crime_type":{"top_hits":{"size":6,"sort":[{"date":{"order":"desc"}}]}}},"meta":{"color":"red"}}"""
  }
}
