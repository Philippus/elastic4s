# Aggregations Reference

```scala
import com.sksamuel.elastic4s.ElasticDsl._
```

Aggregations are added to a `SearchRequest` with `.aggs(...)` or `.aggregations(...)`:

```scala
search("index")
  .query(matchAllQuery())
  .aggs(
    termsAgg("by_status", "status"),
    avgAgg("avg_price", "price")
  )
```

---

## Metric Aggregations

### avgAgg / sumAgg / minAgg / maxAgg

```scala
avgAgg("avg_price", "price")
  .missing(0.0)               // substitute for missing field values
  .script(Script("doc['price'].value * 1.1"))

sumAgg("total_revenue", "price")
minAgg("min_price", "price")
maxAgg("max_price", "price")
```

Result extraction:
```scala
resp.aggs.avg("avg_price").value         // Double (throws if null)
resp.aggs.avg("avg_price").valueOpt      // Option[Double]
resp.aggs.sum("total_revenue").value
resp.aggs.min("min_price").value         // Option[Double]
resp.aggs.max("max_price").value         // Option[Double]
```

### valueCountAgg

```scala
valueCountAgg("count_prices", "price")
```

```scala
resp.aggs.valueCount("count_prices").value  // Double
```

### cardinalityAgg

Approximate distinct count:

```scala
cardinalityAgg("distinct_users", "user_id")
  .precisionThreshold(3000)   // accuracy vs memory trade-off (default 3000, max 40000)
```

```scala
resp.aggs.cardinality("distinct_users").value  // Double
```

### statsAgg / extendedStatsAgg

```scala
statsAggregation("price_stats").field("price")
extendedStatsAgg("price_ext_stats", "price")
```

```scala
val s = resp.aggs.extendedStats("price_ext_stats")
s.count; s.min; s.max; s.avg; s.sum
s.sumOfSquares; s.variance; s.stdDeviation
```

### percentilesAgg

```scala
percentilesAgg("price_percentiles", "price")
  .percents(25.0, 50.0, 75.0, 99.0)
  .compression(100.0)                     // TDigest compression (default 100)
  .hdr(3)                                 // use HDR histogram with N significant digits
  .keyed(false)                           // return as array instead of map
  .format("###.##")
```

```scala
resp.aggs.percentiles("price_percentiles").values  // Map[String, Double]
// e.g. Map("25.0" -> 10.5, "50.0" -> 20.0, ...)
```

### weightedAvgAgg

```scala
weightedAvgAgg(
  "weighted_price",
  value  = WeightedAvgField(field = Some("price")),
  weight = WeightedAvgField(field = Some("sales_count"))
)
```

### topHitsAgg

Returns the top documents within each bucket:

```scala
topHitsAgg("top_per_category")
  .size(3)
  .from(0)
  .sortBy(fieldSort("date").desc())
  .fetchSource(Array("title", "price"), Array.empty)
  .storedFields("title", "date")
  .trackScores(true)
  .version(true)
  .highlighting(highlight("title"))
```

Result via `.result[TopHits]`:
```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopHits

val topHits = bucket.result[TopHits]("top_per_category")
topHits.hits.foreach { hit =>
  println(hit.id)
  println(hit.source)  // Map[String, Any]
}
```

### topMetricsAgg

```scala
topMetricsAgg("latest")
  .field("timestamp")          // sort field
  .size(1)
  .metrics("price", "stock")
```

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopMetrics

val tm = resp.aggs.result[TopMetrics]("latest")
tm.top.foreach(m => println(m.metrics))
```

---

## Bucket Aggregations

### termsAgg

Groups documents by field value:

```scala
termsAgg("by_status", "status")
  .size(10)                                   // top N buckets (default 10)
  .shardSize(100)                             // accuracy vs performance
  .minDocCount(5)                             // exclude rare terms
  .shardMinDocCount(2)
  .missing("unknown")                         // bucket for docs missing the field
  .includeExactValues("published", "draft")   // whitelist exact values
  .excludeExactValues("deleted")
  .includeRegex("pub.*")
  .excludeRegex("test.*")
  .includePartition(0, 4)                     // partition 0 of 4 (for large cardinality)
  .order(TermsOrder("_count", asc = false))   // sort buckets
  .order(TermsOrder("_key", asc = true))
  .collectMode(SubAggCollectionMode.BreadthFirst)
  .executionHint("map")                       // "map" | "global_ordinals"
  .showTermDocCountError(true)
  .script(Script("doc['field'].value"))
  .subAggregations(avgAgg("avg_price", "price"))
```

Result:
```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.Terms

val terms = resp.aggs.result[Terms]("by_status")
terms.buckets.foreach { bucket =>
  println(s"${bucket.key}: ${bucket.docCount}")
  // sub-agg:
  val avg = bucket.avg("avg_price").value
}
terms.docCountErrorUpperBound
terms.otherDocCount
```

### dateHistogramAgg

Groups documents by date interval:

```scala
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval

dateHistogramAgg("by_month", "created_at")
  .calendarInterval(DateHistogramInterval.Month)   // calendar: Second/Minute/Hour/Day/Week/Month/Quarter/Year
  .fixedInterval(DateHistogramInterval.days(7))    // fixed: seconds(n), minutes(n), hours(n), days(n)
  .format("yyyy-MM")
  .timeZone(java.util.TimeZone.getTimeZone("Europe/Paris"))
  .offset("+6h")
  .minDocCount(1)
  .keyed(false)
  .order(HistogramOrder.KEY_ASC)                   // KEY_ASC | KEY_DESC | COUNT_ASC | COUNT_DESC
  .extendedBounds(ElasticDate("2024-01-01"), ElasticDate("2024-12-31"))
  .missing("2024-01-01")
  .subAggregations(sumAgg("revenue", "price"))
```

Result:
```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateHistogram

val dh = resp.aggs.result[DateHistogram]("by_month")
dh.buckets.foreach { bucket =>
  println(s"${bucket.date}: ${bucket.docCount}")  // bucket.date: String (formatted)
  println(bucket.timestamp)                        // Long (epoch ms)
}
```

### histogramAgg

Numeric interval bucketing:

```scala
histogramAggregation("price_ranges")
  .field("price")
  .interval(50.0)
  .minDocCount(1)
  .offset(10.0)
  .extendedBounds(0.0, 500.0)
  .hardBounds(0.0, 1000.0)
  .keyed(false)
  .order(HistogramOrder.KEY_ASC)
  .format("###.##")
```

```scala
resp.aggs.histogram("price_ranges").buckets.foreach { b =>
  println(s"${b.key}: ${b.docCount}")
}
```

### rangeAgg

Custom numeric ranges (half-open intervals):

```scala
rangeAgg("price_tiers", "price")
  .unboundedTo(10.0)               // < 10
  .range(10.0, 50.0)               // [10, 50)
  .range(50.0, 100.0)
  .unboundedFrom(100.0)            // >= 100
  // keyed ranges:
  .range("cheap", 0.0, 10.0)
  .unboundedFrom("expensive", 100.0)
  .keyed(true)
```

```scala
resp.aggs.range("price_tiers").buckets.foreach { b =>
  println(s"${b.from} - ${b.to}: ${b.docCount}")  // from/to: Option[Double]
}
```

### dateRangeAgg

```scala
dateRangeAgg("by_period", "created_at")
  .range("now-1y", "now")
  .range("now-2y", "now-1y")
  .format("yyyy-MM-dd")
```

### filterAgg

Single bucket for documents matching a query:

```scala
filterAgg("published_only", termQuery("status", "published"))
  .subAggregations(avgAgg("avg_price", "price"))
```

```scala
val f = resp.aggs.filter("published_only")
f.docCount
f.avg("avg_price").value
```

### filtersAggregation

Multiple named or unnamed filter buckets:

```scala
// Named (keyed) buckets:
filtersAggregation("by_type")
  .queries(
    "active"   -> termQuery("status", "active"),
    "inactive" -> termQuery("status", "inactive")
  )

// Anonymous buckets (index-based):
filtersAggregation("by_range")
  .queries(
    rangeQuery("price").lte(50),
    rangeQuery("price").gte(50)
  )
```

```scala
resp.aggs.keyedFilters("by_type").aggResults.foreach { case (key, bucket) =>
  println(s"$key: ${bucket.docCount}")
}
resp.aggs.filters("by_range").aggResults.foreach(b => println(b.docCount))
```

### nestedAgg

Aggregates into nested documents:

```scala
nestedAggregation("nested_comments", "comments")
  .subAggregations(
    avgAgg("avg_rating", "comments.rating"),
    termsAgg("top_authors", "comments.author")
  )
```

```scala
val nested = resp.aggs.nested("nested_comments")
nested.avg("avg_rating").value
```

### reverseNestedAggregation

Go back to the parent document from inside a nested aggregation:

```scala
nestedAggregation("by_tag", "tags")
  .subAggregations(
    termsAgg("tag_values", "tags.value")
      .subAggregations(
        reverseNestedAggregation("back_to_root")
          .subAggregations(avgAgg("avg_price", "price"))
      )
  )
```

### childrenAggregation

For parent/child join field documents:

```scala
childrenAggregation("by_answers", "answer")
  .subAggregations(termsAgg("top_authors", "author"))
```

### samplerAgg / diversifiedSamplerAgg

Sample the top-scoring documents per shard before aggregating:

```scala
samplerAgg("sample")
  .shardSize(200)
  .subAggregations(significantTermsAgg("keywords"))

// diversified — max N docs per unique field value:
// (no diversifiedSamplerAgg shorthand — construct directly)
```

### sigTermsAggregation

Statistically significant terms compared to a background set:

```scala
sigTermsAggregation("significant_keywords")
  .field("body")
  .minDocCount(5)
  .size(20)
  .backgroundFilter(termQuery("category", "tech"))
```

### geoDistanceAggregation

```scala
import com.sksamuel.elastic4s.requests.searches.GeoPoint

geoDistanceAggregation("distances")
  .origin(GeoPoint(48.8566, 2.3522))
  .field("location")
  .range(0.0, 100.0)
  .range(100.0, 500.0)
  .unboundedFrom(500.0)
  .unit(DistanceUnit.KM)
```

### geoHashGridAggregation / geoTileGridAggregation

```scala
geoHashGridAggregation("geo_grid")
  .field("location")
  .precision(5)          // 1 (coarsest) to 12 (finest)

geoTileGridAggregation("tile_grid")
  .field("location")
  .precision(8)
```

### autoDateHistogramAgg

Automatically picks the interval to produce approximately N buckets:

```scala
autoDateHistogramAgg("auto_by_date", "created_at")
  .buckets(10)                               // target bucket count (default 10)
  .minimumInterval("day")                    // "second" | "minute" | "hour" | "day" | "month" | "year"
  .format("yyyy-MM-dd")
```

### multiTermsAgg

Composite bucket key from multiple fields:

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.MultiTermsAggregation

multiTermsAgg(
  "by_country_city",
  MultiTermsAggregation.Term().field("country"),
  MultiTermsAggregation.Term().field("city").missing("unknown")
)
  .size(20)
  .minDocCount(1)
  .order(TermsOrder("_count", asc = false))
```

### globalAggregation

Breaks out of any enclosing query context — aggregates over all documents:

```scala
globalAggregation("all_products")
  .subAggregations(avgAgg("global_avg_price", "price"))
```

### missingAgg

Counts documents missing a field:

```scala
missingAgg("no_price", "price")
```

### compositeAgg

Paginate through all buckets (replaces scroll on aggregations):

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.{
  CompositeAggregation, TermsValueSource, DateHistogramValueSource
}

// First page:
search("index").aggs(
  CompositeAggregation("by_cat_date",
    sources = Seq(
      TermsValueSource("category", field = Some("category")),
      DateHistogramValueSource("month", calendarInterval = Some("month"), field = Some("created_at"))
    ),
    size = Some(100)
  )
)

// Next page — pass afterKey from previous result:
import com.sksamuel.elastic4s.requests.searches.aggs.CompositeAggregation._

val result = resp.aggs.compositeAgg("by_cat_date")
result.buckets.foreach(b => println(s"${b.key}: ${b.docCount}"))

val nextPage = result.afterKey  // Option[Map[String, Any]]
nextPage.foreach { after =>
  search("index").aggs(
    CompositeAggregation("by_cat_date", ...).after(after)
  )
}
```

---

## Sub-aggregations

Any bucket aggregation supports nested sub-aggregations via `.subAggregations(aggs*)` or `.subaggs(aggs*)`:

```scala
termsAgg("by_category", "category")
  .size(10)
  .subAggregations(
    avgAgg("avg_price", "price"),
    maxAgg("max_price", "price"),
    dateHistogramAgg("by_month", "created_at")
      .calendarInterval(DateHistogramInterval.Month)
      .subAggregations(sumAgg("monthly_revenue", "price"))
  )
```

Access nested results:
```scala
val terms = resp.aggs.result[Terms]("by_category")
terms.buckets.foreach { bucket =>
  val avg = bucket.avg("avg_price").value
  val dh  = bucket.result[DateHistogram]("by_month")
  dh.buckets.foreach(b => println(b.docCount))
}
```

---

## Pipeline Aggregations

Pipeline aggregations operate on the output of other aggregations. They are added as sub-aggregations of the bucket agg they reference.

### avgBucket / sumBucket / minBucket / maxBucket

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.pipeline._

dateHistogramAgg("by_month", "created_at")
  .calendarInterval(DateHistogramInterval.Month)
  .subAggregations(
    sumAgg("monthly_revenue", "price"),
    AvgBucketPipelineAgg("avg_monthly_revenue", "by_month>monthly_revenue")
  )
```

```scala
resp.aggs.avgBucket("avg_monthly_revenue").value  // Double
resp.aggs.minBucket("min_monthly").value
```

### statsBucket / extendedStatsBucket

```scala
StatsBucketPipelineAgg("revenue_stats", "by_month>monthly_revenue")
ExtendedStatsBucketPipelineAgg("revenue_ext_stats", "by_month>monthly_revenue")
```

```scala
val s = resp.aggs.statsBucket("revenue_stats")
s.count; s.min; s.max; s.avg; s.sum

val es = resp.aggs.extendedStatsBucket("revenue_ext_stats")
es.stdDeviation; es.variance
```

### percentilesBucket

```scala
PercentilesBucketPipelineAgg("revenue_percentiles", "by_month>monthly_revenue")
  .percents(25.0, 50.0, 75.0)
```

```scala
resp.aggs.percentilesBucket("revenue_percentiles").values  // Map[String, Double]
```

### cumulativeSumAgg

```scala
CumulativeSumPipelineAgg("cumulative_revenue", "by_month>monthly_revenue")
```

### derivativeAgg

```scala
DerivativePipelineAgg("revenue_change", "by_month>monthly_revenue")
  .unit("month")   // normalize to "second" | "minute" | "hour" | "day" | "week" | "month" | "year"
```

### movFnAgg (moving function)

```scala
MovFnPipelineAgg(
  name        = "moving_avg",
  bucketsPath = "by_month>monthly_revenue",
  script      = Script("MovingFunctions.unweightedAvg(values)"),
  window      = 3
)
```

```scala
resp.aggs.movFn("moving_avg").value  // Double
```

### bucketScriptAgg

Computes a script across multiple sibling metrics:

```scala
BucketScriptPipelineAgg(
  name         = "profit_margin",
  script       = Script("params.revenue - params.cost"),
  bucketsPaths = Map("revenue" -> "total_revenue", "cost" -> "total_cost")
)
```

### bucketSelectorAgg

Filters out buckets where the script returns false:

```scala
BucketSelectorPipelineAgg(
  name          = "min_revenue_filter",
  script        = Script("params.revenue > 1000"),
  bucketsPathMap = Map("revenue" -> "monthly_revenue")
)
```

### bucketSortAgg

Sort and paginate buckets:

```scala
BucketSortPipelineAgg(
  name = "top_buckets",
  sort = Seq(fieldSort("monthly_revenue").desc()),
  from = Some(0),
  size = Some(5)
)
```

### serialDiffAgg

Subtracts a lagged value from the current value:

```scala
DiffPipelineAgg("weekly_diff", "by_week>count", lag = Some(1))
```

```scala
resp.aggs.serialDiff("weekly_diff").value  // Double
```

---

## Quick reference — all aggregation builders

| Builder | DSL constructor |
|---|---|
| `avgAgg` | `avgAgg(name, field)` |
| `sumAgg` | `sumAgg(name, field)` |
| `minAgg` | `minAgg(name, field)` |
| `maxAgg` | `maxAgg(name, field)` |
| `valueCountAgg` | `valueCountAgg(name, field)` |
| `cardinalityAgg` | `cardinalityAgg(name, field)` |
| `statsAgg` | `statsAggregation(name).field(field)` |
| `extendedStatsAgg` | `extendedStatsAgg(name, field)` |
| `percentilesAgg` | `percentilesAgg(name, field)` |
| `weightedAvgAgg` | `weightedAvgAgg(name, valueField, weightField)` |
| `topHitsAgg` | `topHitsAgg(name)` |
| `topMetricsAgg` | `topMetricsAgg(name)` |
| `termsAgg` | `termsAgg(name, field)` |
| `multiTermsAgg` | `multiTermsAgg(name, terms*)` |
| `dateHistogramAgg` | `dateHistogramAgg(name, field)` |
| `autoDateHistogramAgg` | `autoDateHistogramAgg(name, field)` |
| `histogramAgg` | `histogramAggregation(name).field(f).interval(n)` |
| `rangeAgg` | `rangeAgg(name, field)` |
| `dateRangeAgg` | `dateRangeAgg(name, field)` |
| `filterAgg` | `filterAgg(name, query)` |
| `filtersAgg` | `filtersAggregation(name).queries(...)` |
| `nestedAgg` | `nestedAggregation(name, path)` |
| `reverseNestedAgg` | `reverseNestedAggregation(name)` |
| `childrenAgg` | `childrenAggregation(name, childType)` |
| `globalAgg` | `globalAggregation(name)` |
| `missingAgg` | `missingAgg(name, field)` |
| `samplerAgg` | `samplerAgg(name)` |
| `sigTermsAgg` | `sigTermsAggregation(name)` |
| `geoDistanceAgg` | `geoDistanceAggregation(name).origin(...).field(f)` |
| `geoHashGridAgg` | `geoHashGridAggregation(name)` |
| `geoTileGridAgg` | `geoTileGridAggregation(name)` |
| `compositeAgg` | `CompositeAggregation(name, sources)` |
| `adjacencyMatrix` | `adjacencyMatrixAgg(name, filters)` |
| `variableWidthHistogram` | `variableWidthHistogramAgg(name, field)` |
