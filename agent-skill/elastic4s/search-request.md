# SearchRequest Reference

## Creating a search request

```scala
import com.sksamuel.elastic4s.ElasticDsl._

search("my-index")                          // single index
search("index-a", "index-b")               // multiple indices
search("logs-*")                            // wildcard pattern
search(Seq("index-a", "index-b"))           // from Iterable
```

---

## Query

```scala
search("index").query(matchQuery("title", "elasticsearch"))

// Shorthand methods directly on SearchRequest:
search("index").query("some text")                     // SimpleStringQuery
search("index").matchQuery("title", "elasticsearch")
search("index").matchAllQuery()
search("index").termQuery("status", "published")
search("index").prefix("slug", "elast")
search("index").rawQuery("""{"term":{"status":"published"}}""")

// Block form (for readability with complex queries):
search("index").bool {
  boolQuery()
    .must(matchQuery("body", "elasticsearch"))
    .filter(termQuery("status", "published"))
}
```

---

## Pagination

```scala
search("index").from(0).size(20)   // page 1 of 20
search("index").from(20).size(20)  // page 2

// Aliases:
search("index").start(0).limit(20) // from/size aliases
```

Default `size` is 10. Default `from` is 0.

For deep pagination prefer [search-after or PIT](pagination.md) — `from` + `size` is inefficient beyond ~10,000 hits.

---

## Sorting

```scala
import com.sksamuel.elastic4s.requests.searches.sort._

search("index").sortBy(
  fieldSort("date").desc(),
  fieldSort("_score").asc()
)
```

### fieldSort

```scala
fieldSort(field: String): FieldSort

fieldSort("date")
  .asc()  / .desc()
  .order(SortOrder.Asc)         // SortOrder.Asc | SortOrder.Desc
  .missing("_last")             // "_last" | "_first" | any value
  .unmappedType("date")         // ignore missing mappings, treat as this type
  .mode(SortMode.Min)           // Min | Max | Sum | Avg | Median — for multi-value fields
  .numericType("long")          // coerce numeric type: "long" | "double" | "date" | "date_nanos"
  .nested(NestedSort()          // sort within nested objects
    .path("comments")
    .filter(termQuery("comments.verified", true))
    .maxChildren(10))

// Shortcuts on SearchRequest:
search("index").sortByFieldAsc("price")
search("index").sortByFieldDesc("date")
```

### scoreSort

```scala
scoreSort()              // default Desc
scoreSort(SortOrder.Asc) // ascending relevance (least relevant first)

search("index").sortBy(fieldSort("date").desc(), scoreSort())
```

### geoDistanceSort

```scala
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.common.DistanceUnit

GeoDistanceSort(field = "location", points = Seq(GeoPoint(48.8566, 2.3522)))
  .asc()
  .unit(DistanceUnit.KM)
  .mode(SortMode.Min)
  .geoDistance(GeoDistance.Arc)      // Arc | Plane
  .ignoreUnmapped(false)
  .nested(NestedSort().path("offices"))
```

### scriptSort

```scala
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.sort.ScriptSortType

ScriptSort(
  script = Script("doc['price'].value * params.factor").params(Map("factor" -> 1.2)),
  scriptSortType = ScriptSortType.Number   // Number | String
).asc()
 .mode(SortMode.Avg)
```

---

## Aggregations

```scala
search("index")
  .aggs(termsAgg("by_status", "status"), avgAgg("avg_price", "price"))
  .aggregations(...)   // alias for .aggs(...)
```

See [aggregations.md](aggregations.md) for the full reference.

---

## Source filtering

```scala
// Disable _source entirely
search("index").fetchSource(false)

// Include only specific fields
search("index").sourceInclude("title", "date", "author")

// Exclude fields
search("index").sourceExclude("body", "raw_content")

// Include + exclude together
search("index").sourceFiltering(
  includes = Seq("title", "date"),
  excludes = Seq("internal_*")
)

// Via FetchSourceContext directly
import com.sksamuel.elastic4s.requests.common.FetchSourceContext
search("index").fetchContext(FetchSourceContext(
  fetchSource = true,
  includes    = Array("title", "date"),
  excludes    = Array("body")
))
```

---

## Stored fields

Returns fields stored separately (mapped with `store: true`), instead of reading from `_source`:

```scala
search("index").storedFields("title", "date")
```

Access in response: `hit.storedField("title").value`

---

## Doc values fields

Returns the doc values representation of a field (efficient for keyword / numeric fields):

```scala
search("index").docValues("category", "price")
```

---

## Script fields

Compute a value at query time for each hit:

```scala
import com.sksamuel.elastic4s.requests.script.Script

search("index").scriptfields(
  scriptField("price_with_tax", Script("doc['price'].value * 1.2")),
  scriptField("days_old",       Script("(new Date().getTime() - doc['created_at'].value.millis) / 86400000"))
)
```

Access in response: `hit.sourceField("price_with_tax").value`

---

## Highlighting

```scala
search("index")
  .query(matchQuery("body", "elasticsearch"))
  .highlighting(
    highlight("title").fragmentSize(150).numberOfFragments(3),
    highlight("body").fragmentSize(300).numberOfFragments(5)
  )
```

### HighlightField options

```scala
highlight(field: String): HighlightField

highlight("body")
  .fragmentSize(150)          // character size of each fragment
  .numberOfFragments(3)       // max number of fragments returned
  .noMatchSize(100)           // chars to return when no match (0 = disabled)
  .order("score")             // "score" | "none"
  .highlighterType("unified") // "unified" (default) | "plain" | "fvh"
  .preTags("<em>", "<strong>")
  .postTags("</em>", "</strong>")
  .requireFieldMatch(false)   // highlight even if query doesn't reference this field
  .fragmenter("span")         // "simple" | "span"
  .fragmentOffset(0)
  .query(matchQuery("body", "override query for highlighting"))
  .matchedFields("body", "body.english")  // combine multiple analysed fields (fvh only)
  .boundaryChars(".,!? \t\n")
  .boundaryMaxScan(20)
  .boundaryScanner("sentence")
  .boundaryScannerLocale("en-US")
  .phraseLimit(256)
  .maxAnalyzedOffset(1000000)
  .tagsSchema("styled")       // built-in styled tags schema
```

### Global highlight options

```scala
search("index").highlighting(
  options = HighlightOptions()
    .encoder("html")            // "default" | "html"
    .tagsSchema("styled")
    .requireFieldMatch(false)
    .highlighterType("unified")
    .fragmentSize(150)
    .numOfFragments(3)
    .preTags("<em>")
    .postTags("</em>")
    .order("score"),
  fields = Seq(
    highlight("title"),
    highlight("body")
  )
)
```

Access in response:
```scala
hit.highlight                        // Map[String, Seq[String]]
hit.highlight.getOrElse("body", Nil) // Seq[String]
```

---

## Post filter

Applied **after** aggregations are computed — documents filtered out here are still counted in aggregations:

```scala
search("index")
  .query(matchQuery("body", "elasticsearch"))
  .aggs(termsAgg("by_status", "status"))
  .postFilter(termQuery("status", "published"))   // filters hits but not aggregations
```

---

## Field collapsing

Returns only one document per unique value of a field (the top-scoring one):

```scala
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseRequest

search("index")
  .query(matchAllQuery())
  .collapse(CollapseRequest("user_id")
    .inner(innerHits("top_3_per_user").size(3).sortBy(fieldSort("date").desc()))
    .maxConcurrentGroupSearches(4))
```

---

## Rescore

Re-ranks the top N hits with a secondary (more expensive) query:

```scala
search("index")
  .query(matchQuery("body", "elasticsearch"))
  .rescore(
    rescore(matchPhraseQuery("body", "elasticsearch guide"))
      .window(100)                              // number of top hits to rescore
      .originalQueryWeight(0.7)                // weight of original query score
      .rescoreQueryWeight(1.2)                 // weight of rescore query score
      .scoreMode(QueryRescoreMode.Total)        // Avg | Max | Min | Total | Multiply
  )
```

Multiple rescorers are applied in order:

```scala
search("index").rescore(
  rescore(matchPhraseQuery("title", "foo")).window(200),
  rescore(functionScoreQuery()).window(50)
)
```

---

## Min score

Exclude documents whose score falls below a threshold:

```scala
search("index").query(matchQuery("body", "elasticsearch")).minScore(0.5)
```

---

## Explain, version, sequence number

```scala
search("index")
  .explain(true)          // include score explanation per hit
  .version(true)          // include _version in each hit
  .seqNoPrimaryTerm(true) // include _seq_no and _primary_term per hit
```

---

## Track scores and track hits

```scala
search("index")
  .sortBy(fieldSort("date").desc())
  .trackScores(true)        // compute scores even when sorting by a field

search("index")
  .trackTotalHits(true)     // accurate total count (may be slow on large indices)
  .trackTotalHits(10000L)   // count up to N, then return "gte" relation
```

---

## Timeout and terminate after

```scala
import scala.concurrent.duration._

search("index").timeout(5.seconds)    // abort after duration, return partial results
search("index").terminateAfter(1000)  // stop collecting after N docs per shard
```

---

## Search after (stateless deep pagination)

Requires a stable sort with a unique tiebreaker (typically `_id` or `_seq_no`):

```scala
// First page
val resp1 = client.execute {
  search("index")
    .query(matchAllQuery())
    .sortBy(fieldSort("date").desc(), fieldSort("_id").asc())
    .size(10)
}.await

// Subsequent pages — pass sort values of the last hit
val lastHit   = resp1.result.hits.hits.last
val sortValues = lastHit.sort.get   // Seq[AnyRef]

val resp2 = client.execute {
  search("index")
    .query(matchAllQuery())
    .sortBy(fieldSort("date").desc(), fieldSort("_id").asc())
    .size(10)
    .searchAfter(sortValues)
}.await
```

See [pagination.md](pagination.md) for scroll and PIT patterns.

---

## Scroll (stateful pagination)

```scala
search("index")
  .query(matchQuery("body", "elasticsearch"))
  .size(100)
  .keepAlive("1m")   // or .scroll("1m") or .scroll(1.minute)
```

See [pagination.md](pagination.md) for the full scroll continuation pattern.

---

## Point in Time

```scala
import com.sksamuel.elastic4s.requests.searches.Pit
import scala.concurrent.duration._

search("index")
  .query(matchAllQuery())
  .pit(Pit("pit-id-from-open-response", keepAlive = Some(1.minute)))
  .sortBy(fieldSort("_shard_doc").asc())
```

When `.pit(...)` is set, the index in `search(...)` is ignored automatically.

See [pagination.md](pagination.md) for the open/close PIT lifecycle.

---

## Slice (parallel scroll)

Split a scroll across N parallel consumers:

```scala
search("index")
  .query(matchAllQuery())
  .keepAlive("1m")
  .size(500)
  .slice(id = 0, max = 4)   // consumer 0 of 4
```

Each consumer uses a different `id` (0 to max-1) and the same `max`.

---

## Profile

Adds detailed timing information to the response:

```scala
search("index").query(matchAllQuery()).profile(true)
// response.result.profile — Option[ProfileResults]
```

---

## Routing and preference

```scala
search("index").routing("user-123")          // route to specific shards

search("index").preference("_local")         // prefer local shards
search("index").preference("_primary")       // primary shards only
search("index").preference("custom-value")   // sticky session
```

---

## Index boost

Apply per-index score multipliers when searching across multiple indices:

```scala
search("index-a", "index-b")
  .query(matchAllQuery())
  .indexBoost(Map("index-a" -> 1.5, "index-b" -> 0.8))
  // or:
  .indexBoost("index-a" -> 1.5, "index-b" -> 0.8)
```

---

## KNN (vector search)

```scala
import com.sksamuel.elastic4s.requests.searches.knn.{Knn, QueryVectorBuilder}

// Exact vector
search("index").knn(
  Knn(field = "embedding")
    .queryVector(Seq(0.1, 0.2, 0.3, 0.4))
    .k(10)
    .numCandidates(100)
    .filter(termQuery("status", "published"))
    .similarity(0.8f)
    .boost(1.0)
)

// With model inference
search("index").knn(
  Knn(field = "embedding")
    .queryVectorBuilder(QueryVectorBuilder(modelId = "my-model", modelText = "search query"))
    .k(10)
    .numCandidates(100)
)

// Multiple KNN queries
search("index").multipleKnn(Seq(
  Knn("embedding-a").queryVector(vec1).k(5).numCandidates(50),
  Knn("embedding-b").queryVector(vec2).k(5).numCandidates(50)
))
```

---

## Raw JSON body

Bypass the DSL entirely and pass a raw Elasticsearch request body:

```scala
search("index").source("""
{
  "query": { "match": { "title": "elasticsearch" } },
  "size": 10
}
""")
```

Note: when `.source(...)` is set, all other body-level settings are ignored. HTTP query parameters (routing, preference…) still apply.

---

## Multi-search

Execute multiple searches in a single round-trip:

```scala
val resp = client.execute {
  multi(
    search("index").query(matchQuery("title", "elasticsearch")),
    search("index").query(termQuery("status", "published")).size(5)
  )
}.await

resp.result.items.foreach {
  case Right(r) => println(r.hits.total.value)
  case Left(e)  => println(s"error: ${e.error.reason}")
}
```

---

## Quick reference — all SearchRequest methods

| Method | Description |
|--------|-------------|
| `.query(q)` | Set the main query |
| `.postFilter(q)` | Filter hits after aggregations |
| `.from(n).size(n)` | Offset + page size |
| `.sortBy(sorts*)` | Add sort criteria |
| `.aggs(aggs*)` | Add aggregations |
| `.highlighting(fields*)` | Highlight configuration |
| `.sourceInclude(fields*)` | Include _source fields |
| `.sourceExclude(fields*)` | Exclude _source fields |
| `.fetchSource(false)` | Disable _source |
| `.storedFields(fields*)` | Return stored fields |
| `.docValues(fields*)` | Return doc values |
| `.scriptfields(fields*)` | Computed fields |
| `.collapse(CollapseRequest)` | Field collapsing |
| `.rescore(rescorers*)` | Re-rank top N hits |
| `.minScore(n)` | Minimum score threshold |
| `.explain(bool)` | Include score explanation |
| `.version(bool)` | Include `_version` |
| `.seqNoPrimaryTerm(bool)` | Include `_seq_no` + `_primary_term` |
| `.trackScores(bool)` | Compute scores when sorting by field |
| `.trackTotalHits(bool/Long)` | Accurate total count |
| `.timeout(duration)` | Abort after duration |
| `.terminateAfter(n)` | Stop after N docs/shard |
| `.searchAfter(values*)` | Deep pagination cursor |
| `.keepAlive(duration)` | Enable scroll |
| `.slice(id, max)` | Parallel scroll slice |
| `.pit(Pit)` | Use a point-in-time |
| `.profile(bool)` | Include profiling info |
| `.routing(value)` | Shard routing key |
| `.preference(value)` | Shard preference |
| `.indexBoost(map)` | Per-index score multiplier |
| `.knn(Knn)` | Vector search |
| `.source(json)` | Raw JSON body |
| `.requestCache(bool)` | Control request caching |
| `.allowPartialSearchResults(bool)` | Allow shard failures |
