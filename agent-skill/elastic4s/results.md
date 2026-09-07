# Result Extraction Reference

---

## Response wrapper

`client.execute(...)` returns `F[Response[U]]`. `Response[U]` is a sealed trait:

```scala
import com.sksamuel.elastic4s.{RequestSuccess, RequestFailure}

client.execute { search("index").query(matchAllQuery()) }.foreach {
  case RequestSuccess(status, body, headers, result) =>
    println(result.hits.total.value)
  case RequestFailure(status, body, headers, error) =>
    println(s"${error.`type`}: ${error.reason}")
    error.rootCause.foreach(c => println(c.reason))
}

// Direct access (throws on failure — suitable for tests):
val result: SearchResponse = response.result
```

`ElasticError` fields:

| Field | Type | Description |
|---|---|---|
| `error.type` | `String` | e.g. `"index_not_found_exception"` |
| `error.reason` | `String` | human-readable message |
| `error.rootCause` | `Seq[ElasticError]` | underlying causes |

---

## SearchResponse

```scala
val resp: SearchResponse = response.result

resp.took                  // Long — time in milliseconds
resp.isTimedOut            // Boolean
resp.isTerminatedEarly     // Boolean (terminateAfter was reached)
resp.scrollId              // Option[String] — present when scroll enabled
resp.pitId                 // Option[String] — present when PIT enabled

resp.shards.total          // Int
resp.shards.successful     // Int
resp.shards.failed         // Int

resp.totalHits             // Long — shortcut for hits.total.value
resp.maxScore              // Double — shortcut for hits.maxScore
resp.size                  // Long — number of hits returned
resp.ids                   // Seq[String] — list of _id values
resp.isEmpty               // Boolean
resp.nonEmpty              // Boolean

resp.hits                  // SearchHits
resp.aggs                  // Aggregations (alias: resp.aggregations)
resp.aggregationsAsMap     // Map[String, Any] — raw aggregation data
resp.aggregationsAsString  // String — JSON representation of aggregations
```

---

## SearchHits

```scala
val hits: SearchHits = resp.hits

hits.total.value     // Long — total matching documents
hits.total.relation  // String — "eq" (exact) or "gte" (approximate, when trackTotalHits is capped)
hits.maxScore        // Double — score of the highest-scoring hit
hits.hits            // Array[SearchHit]
hits.size            // Long — count of hits in this page
hits.isEmpty
hits.nonEmpty
```

---

## SearchHit

```scala
val hit: SearchHit = resp.hits.hits.head

// Identity
hit.id             // String  — document _id
hit.index          // String  — index name
hit.version        // Long    — document version (-1 if not requested)
hit.seqNo          // Long    — sequence number (-1 if not requested)
hit.primaryTerm    // Long    — primary term (-1 if not requested)
hit.score          // Float   — relevance score (NaN if sorted by field without trackScores)
hit.ref            // DocumentRef(index, id)

// Source
hit.sourceAsString              // String — raw JSON
hit.sourceAsMap                 // Map[String, AnyRef]
hit.sourceAsBytes               // Array[Byte]
hit.sourceField("price")        // AnyRef — single field (throws if absent)
hit.sourceFieldOpt("price")     // Option[AnyRef]
hit.isSourceEmpty               // Boolean

// Stored fields (requires .storedFields(...) on the request)
hit.storedField("title")        // HitField (throws if absent)
hit.storedFieldOpt("title")     // Option[HitField]

// HitField access:
val f: HitField = hit.storedField("title")
f.value                         // AnyRef — first value
f.values                        // Seq[AnyRef] — all values (multi-value fields)
f.name                          // String

// Highlighting (requires .highlighting(...) on the request)
hit.highlight                              // Map[String, Seq[String]]
hit.highlightFragments("body")             // Seq[String] — fragments for a field

// Inner hits (requires innerHits on nested/has-child query)
hit.innerHits                              // Map[String, InnerHits]
val inner: InnerHits = hit.innerHits("comments")
inner.total.value                          // Long
inner.hits.foreach { ih: InnerHit =>
  ih.id; ih.index; ih.score
  ih.source                                // Map[String, AnyRef]
  ih.highlight                             // Map[String, Seq[String]]
  ih.fields                                // doc values / stored fields
  ih.sort                                  // Seq[AnyRef] — sort values
  ih.nested                                // Map[String, AnyRef] — nested offset info
}

// Sort values (for search_after pagination)
hit.sort                          // Option[Seq[AnyRef]]
val sortValues = hit.sort.get     // pass to .searchAfter(sortValues) on next request

// Score explanation (requires .explain(true) on the request)
hit.explanation                   // Option[Explanation]

// Matched query names (requires .queryName(...) on individual queries)
hit.matchedQueries                // Option[Set[String]]

// Routing / shard metadata
hit.routing                       // Option[String]
hit.shard                         // Option[String]
hit.node                          // Option[String]
```

---

## Type-safe mapping with HitReader

`HitReader[T]` is a typeclass that converts a `SearchHit` to a domain type. It is resolved implicitly when calling `.to[T]` or `.safeTo[T]`.

### Automatic derivation via JSON modules

**Jackson** (recommended for Scala case classes):

```scala
// SBT: "com.sksamuel.elastic4s" %% "elastic4s-json-jackson" % version
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

case class Article(title: String, price: Double, status: String)

val articles: IndexedSeq[Article]           = resp.to[Article]
val safe:     IndexedSeq[Try[Article]]      = resp.safeTo[Article]
```

Jackson automatically maps `_id`, `_index`, `_version`, `_seq_no`, `_primary_term` into the case class if the field is present. `FAIL_ON_UNKNOWN_PROPERTIES` is disabled.

**Circe**:

```scala
// SBT: "com.sksamuel.elastic4s" %% "elastic4s-json-circe" % version
import io.circe.generic.auto._
import com.sksamuel.elastic4s.circe._

val articles: IndexedSeq[Article] = resp.to[Article]
```

**Play JSON**:

```scala
import play.api.libs.json._
import com.sksamuel.elastic4s.playjson._

implicit val fmt: Format[Article] = Json.format[Article]
val articles = resp.to[Article]
```

### Manual implementation

```scala
import com.sksamuel.elastic4s.{Hit, HitReader}
import scala.util.Try

case class Article(id: String, title: String, price: Double)

implicit val reader: HitReader[Article] = (hit: Hit) =>
  Try {
    Article(
      id    = hit.id,
      title = hit.sourceField("title").toString,
      price = hit.sourceField("price").toString.toDouble
    )
  }

val articles: IndexedSeq[Article]      = resp.to[Article]
val safe:     IndexedSeq[Try[Article]] = resp.safeTo[Article]
```

### Per-hit access

```scala
resp.hits.hits.foreach { hit =>
  val article: Article      = hit.to[Article]       // throws on failure
  val safe:    Try[Article] = hit.safeTo[Article]   // wraps in Try
  val opt:     Option[Article] = hit.toOpt[Article] // None if hit doesn't exist
}
```

---

## Aggregation results

Access aggregations via `resp.aggs` (a `HasAggregations` instance). The accessor method depends on the aggregation type.

### Generic accessor

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.AggSerde

resp.aggs.result[T](name)(implicit serde: AggSerde[T]): T
```

All named aggregation result types have an implicit `AggSerde` in their companion object.

### Metric aggregations

```scala
resp.aggs.avg("avg_price").value              // Double (throws if null)
resp.aggs.avg("avg_price").valueOpt           // Option[Double]
resp.aggs.avg("avg_price").valueAsString      // Option[String]

resp.aggs.sum("total").value                  // Double
resp.aggs.min("min_price").value              // Option[Double]
resp.aggs.max("max_price").value              // Option[Double]

resp.aggs.cardinality("distinct_users").value // Double

resp.aggs.valueCount("count").value           // Double

val stats = resp.aggs.extendedStats("stats")
stats.count; stats.min; stats.max; stats.avg; stats.sum
stats.sumOfSquares; stats.variance; stats.stdDeviation

resp.aggs.percentiles("pcts").values          // Map[String, Double] — key is percentile as String
// e.g. Map("25.0" -> 10.5, "50.0" -> 20.0, "75.0" -> 45.0)

val bounds = resp.aggs.geoBounds("bbox")
bounds.topLeft     // Option[GeoPoint]
bounds.bottomRight // Option[GeoPoint]

val centroid = resp.aggs.geoCentroid("center")
centroid.centroid  // Option[GeoPoint]
centroid.count     // Long
```

**topHits** — requires `import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopHits`:

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.{TopHits, TopHit}

val th: TopHits = resp.aggs.result[TopHits]("top_hits")
th.total.value
th.maxScore
th.hits.foreach { hit: TopHit =>
  hit.id; hit.index; hit.score
  hit.source                  // Map[String, Any]
  hit.to[Article]             // type-safe (requires AggReader[Article])
}
```

**topMetrics** — requires `import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopMetrics`:

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopMetrics

val tm: TopMetrics = resp.aggs.result[TopMetrics]("top_metrics")
tm.top.foreach(m => println(m.metrics))  // Map[String, Any]
```

### Bucket aggregations — Terms

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.Terms

val terms: Terms = resp.aggs.result[Terms]("by_status")
terms.docCountErrorUpperBound  // Long
terms.otherDocCount            // Long (sum_other_doc_count)

terms.buckets.foreach { bucket =>
  bucket.key                   // String
  bucket.docCount              // Long
  // sub-aggregation on the bucket:
  bucket.avg("avg_price").value
  bucket.result[Terms]("nested_terms")
}

// Look up a specific bucket by key:
terms.bucket("published")         // TermBucket (throws if absent)
terms.bucketOpt("published")      // Option[TermBucket]
```

### Bucket aggregations — DateHistogram

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateHistogram

val dh: DateHistogram = resp.aggs.result[DateHistogram]("by_month")
dh.buckets.foreach { bucket =>
  bucket.date       // String — formatted date (e.g. "2024-01")
  bucket.timestamp  // Long   — epoch milliseconds
  bucket.docCount   // Long
  // sub-agg:
  bucket.sum("revenue").value
}
```

### Bucket aggregations — Histogram

```scala
resp.aggs.histogram("price_ranges").buckets.foreach { bucket =>
  bucket.key        // Double — bucket key
  bucket.docCount   // Long
}
```

### Bucket aggregations — Range

```scala
resp.aggs.range("price_tiers").buckets.foreach { bucket =>
  bucket.key        // Option[String] — label if keyed
  bucket.from       // Option[Double]
  bucket.to         // Option[Double]
  bucket.docCount   // Long
}
```

### Bucket aggregations — Filter / Filters

```scala
val f = resp.aggs.filter("published")
f.docCount
f.avg("avg_price").value   // sub-agg

// Keyed (named) filters:
resp.aggs.keyedFilters("by_type").aggResults.foreach { case (key, bucket) =>
  println(s"$key: ${bucket.docCount}")
}

// Anonymous filters:
resp.aggs.filters("by_range").aggResults.foreach(b => println(b.docCount))
```

### Bucket aggregations — Nested / ReverseNested

```scala
val nested = resp.aggs.nested("comments_nested")
nested.avg("avg_rating").value

val rev = resp.aggs.reverseNested("back_to_root")
rev.avg("parent_price").value
```

### Bucket aggregations — Children

```scala
val children = resp.aggs.children("answers")
children.docCount
children.result[Terms]("top_authors")
```

### Bucket aggregations — SignificantTerms

```scala
val sig = resp.aggs.significantTerms("keywords")
sig.docCount    // Long — background count
sig.bgCount     // Long
sig.buckets.foreach { bucket =>
  bucket.key        // String
  bucket.docCount   // Long
  bucket.bgCount    // Long — background doc count
  bucket.score      // Double — significance score
}
```

### Bucket aggregations — AdjacencyMatrix

```scala
resp.aggs.adjacencyMatrixAgg("connections").buckets.foreach { bucket =>
  bucket.key        // String — matrix key (e.g. "filterA&filterB")
  bucket.docCount   // Long
}
```

### Bucket aggregations — Composite

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.CompositeAggregation._

val composite = resp.aggs.compositeAgg("by_cat_date")
composite.buckets.foreach { bucket =>
  bucket.key       // Map[String, Any] — e.g. Map("category" -> "tech", "month" -> "2024-01")
  bucket.docCount  // Long
  bucket.avg("avg_price").value  // sub-agg
}

// Pagination: pass afterKey to next request
composite.afterKey  // Option[Map[String, Any]]
```

### Pipeline aggregations

```scala
resp.aggs.avgBucket("avg_monthly")                  // AvgBucketAggResult
resp.aggs.minBucket("min_monthly").value            // Double
resp.aggs.statsBucket("stats").count                // Long
resp.aggs.extendedStatsBucket("ext_stats").stdDeviation
resp.aggs.percentilesBucket("pct_bucket").values    // Map[String, Double]
resp.aggs.movFn("moving_avg").value                 // Double
resp.aggs.serialDiff("diff").value                  // Double
```

---

## AggReader — type-safe bucket content

Inside a bucket or top-hit, use `to[T]` / `safeTo[T]` with an implicit `AggReader[T]`:

```scala
import com.sksamuel.elastic4s.AggReader

implicit val reader: AggReader[Article] = json =>
  Try(JacksonSupport.mapper.readValue[Article](json))

// Then on any Transformable (TermBucket, InnerHit, TopHit…):
bucket.to[Article]       // T (throws)
bucket.safeTo[Article]   // Try[T]
```

---

## Suggestions

```scala
resp.termSuggestion("did_you_mean")         // Map[String, TermSuggestionResult]
resp.completionSuggestion("autocomplete")   // Map[String, CompletionSuggestionResult]
resp.phraseSuggestion("phrase_suggest")     // Map[String, PhraseSuggestionResult]

// Access per input text:
val s = resp.termSuggestion("did_you_mean")("elasticserch")
s.options.foreach { opt =>
  opt.text    // String — suggestion
  opt.score   // Double
  opt.freq    // Int
}
```

---

## Quick reference — result types

| Accessor | Type | Description |
|---|---|---|
| `resp.hits.total` | `Total` | `value: Long`, `relation: String` |
| `resp.hits.hits` | `Array[SearchHit]` | per-document results |
| `hit.sourceAsMap` | `Map[String, AnyRef]` | raw source as map |
| `hit.to[T]` | `T` | type-safe (throws) |
| `hit.safeTo[T]` | `Try[T]` | type-safe (wrapped) |
| `hit.storedField(name)` | `HitField` | stored field value(s) |
| `hit.highlight` | `Map[String, Seq[String]]` | highlight fragments |
| `hit.innerHits` | `Map[String, InnerHits]` | nested/child inner hits |
| `hit.sort` | `Option[Seq[AnyRef]]` | sort values (for search_after) |
| `resp.aggs.result[T](name)` | `T` | typed agg result via AggSerde |
| `resp.aggs.avg(name)` | `AvgAggResult` | `.value: Double`, `.valueOpt` |
| `resp.aggs.result[Terms](name)` | `Terms` | `.buckets: Seq[TermBucket]` |
| `resp.aggs.result[DateHistogram](name)` | `DateHistogram` | `.buckets: Seq[DateHistogramBucket]` |
| `resp.aggs.filter(name)` | `FilterAggregationResult` | `.docCount`, sub-aggs |
| `resp.aggs.nested(name)` | `NestedAggResult` | sub-agg container |
| `resp.aggs.compositeAgg(name)` | `CompositeAggregationResult` | `.buckets`, `.afterKey` |
