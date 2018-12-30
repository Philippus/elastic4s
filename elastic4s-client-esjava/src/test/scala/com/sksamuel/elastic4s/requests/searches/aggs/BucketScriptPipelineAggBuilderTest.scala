package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.http.JsonSugar
import com.sksamuel.elastic4s.requests.searches.{DateHistogramInterval, SearchBodyBuilderFn, SearchRequest}
import org.scalatest.FlatSpec


class BucketScriptPipelineAggBuilderTest extends FlatSpec with JsonSugar  {

  import com.sksamuel.elastic4s.ElasticDsl._

  /**
    * Took example query from www.elastic.co
    * https://www.elastic.co/guide/en/elasticsearch/reference/6.1/search-aggregations-pipeline-bucket-script-aggregation.html
    *
    */
  "AggregationBuilderFn" should "generate correct bucketScript aggregation json" in {

    val search = SearchRequest("myindex" / "mytype").aggs(
      dateHistogramAgg("sales_per_month", "date").interval(DateHistogramInterval.Month).subaggs(
        sumAgg("total_sales", "price"),
        filterAgg("t-shirts", termQuery("type", "t-shirt")).addSubagg(
          sumAgg("sales", "price")
        )
      ),
      bucketScriptAggregation(
        "t-shirt-percentage",
        script("params.tShirtSales / params.totalSales * 100"),
        Map(
          "tShirtSales" -> "t-shirts>sales",
          "totalSales" -> "total_sales"
        )
      )
    )

    SearchBodyBuilderFn(search).string() should matchJsonResource("/search/aggs/bucket_script_pipeline_query.json")
  }
}
