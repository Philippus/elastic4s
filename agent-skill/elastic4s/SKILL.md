---
name: elastic4s
description: elastic4s DSL reference for writing Elasticsearch queries, aggregations, index management, and retrieving typed results in Scala. Use when writing SearchRequest, query builders (boolQuery, matchQuery, termQuery, rangeQuery, nestedQuery…), aggregation pipelines (termsAgg, dateHistogramAgg…), result extraction (SearchResponse, HitReader, to[T]), client setup, pagination patterns (scroll, search-after, PIT), index/mapping management (createIndex, putMapping, indexInto, updateById, deleteById), or debugging (explain, validate, analyze, profile).
when_to_use: Invoke for any elastic4s Scala code — query composition, aggregation pipelines, response mapping, pagination, sorting, highlighting, client configuration, index and mapping management, document CRUD, or debugging queries. Triggers on imports of com.sksamuel.elastic4s or ElasticDsl.
paths: "**/*.scala build.sbt"
---

# elastic4s — DSL Reference

## Imports

```scala
import com.sksamuel.elastic4s.ElasticDsl._                      // all DSL methods
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.http.JavaClient
```

## Quick start

```scala
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, RequestSuccess, RequestFailure}
import com.sksamuel.elastic4s.http.JavaClient
import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits.catsStdInstancesForFuture

val client = ElasticClient(JavaClient(ElasticProperties("http://localhost:9200")))

val future = client.execute {
  search("my-index")
    .query(matchQuery("title", "elasticsearch"))
    .size(10)
}

future.foreach {
  case RequestSuccess(_, _, _, result) =>
    result.hits.hits.foreach(h => println(h.sourceAsString))
  case RequestFailure(_, _, _, error) =>
    println(s"Error: ${error.reason}")
}
```

## Query cheat sheet

```scala
// Full-text
matchQuery("field", "value")
matchPhraseQuery("field", "exact phrase")
multiMatchQuery("text").fields("title", "body")
queryStringQuery("title:foo AND body:bar")
simpleStringQuery("foo bar")          // implicit: "foo bar" also works

// Term-level
termQuery("status", "published")
termsQuery("status", "published", "draft")
rangeQuery("price").gte(10).lte(100)
rangeQuery("date").gte("2024-01-01").format("yyyy-MM-dd")
existsQuery("thumbnail")
wildcardQuery("name", "el*tic")
prefixQuery("slug", "elast")
regexQuery("code", "[A-Z]{3}-[0-9]+")
fuzzyQuery("title", "elasticearch")   // tolerates typos
idsQuery("id1", "id2", "id3")

// Compound
boolQuery()
  .must(matchQuery("body", "elasticsearch"))
  .filter(termQuery("status", "published"))
  .should(termQuery("featured", true))
  .mustNot(existsQuery("deleted_at"))
  .minimumShouldMatch(1)

// Shortcuts
must(matchQuery("a", "b"), termQuery("c", "d"))
filter(termQuery("status", "published"), rangeQuery("date").gte("now-7d"))
should(termQuery("cat", "a"), termQuery("cat", "b"))
```

## SearchRequest cheat sheet

```scala
search("index")                              // single index
search("index1", "index2")                  // multi-index
search("index-*")                           // wildcard

search("index")
  .query(boolQuery())                        // set query
  .postFilter(termQuery("visible", true))    // filter after aggs
  .from(0).size(20)                          // pagination
  .sortBy(fieldSort("date").desc())          // sorting
  .sortBy(scoreSort())                       // by relevance
  .aggs(termsAgg("by_cat", "category"))      // aggregations
  .sourceInclude("title", "date")            // source filtering
  .sourceExclude("body")
  .minScore(0.5)
  .explain(true)
  .trackTotalHits(true)
```

## Result extraction cheat sheet

```scala
val resp = response.result                   // SearchResponse

// Hits
resp.hits.total.value                        // Long: total documents
resp.hits.total.relation                     // "eq" or "gte"
resp.hits.maxScore                           // Option[Float]
resp.hits.hits                               // Seq[SearchHit]

// Per hit
val hit = resp.hits.hits.head
hit._id; hit._index; hit._score
hit.sourceAsString                           // raw JSON string
hit.sourceAsMap                              // Map[String, Any]
hit.sourceField("title").value               // single field (raw)

// Type-safe mapping (requires HitReader[T] in scope)
resp.to[MyCase]                              // Seq[MyCase]
resp.safeTo[MyCase]                          // Seq[Either[Throwable, MyCase]]

// Aggregations
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.Terms
resp.aggs.result[Terms]("by_cat").buckets
  .foreach(b => println(s"${b.key}: ${b.docCount}"))
```

## Supporting files

Read these files for the full reference when writing more complex code:

- [client.md](client.md) — client backends (JavaClient, Pekko, Sttp, http4s, ZIO) and effect types
- [queries.md](queries.md) — complete query DSL with all options and modifiers
- [search-request.md](search-request.md) — SearchRequest options: sorts, highlights, collapse, rescore, PIT…
- [aggregations.md](aggregations.md) — all metric, bucket, and pipeline aggregations
- [results.md](results.md) — SearchResponse, SearchHit, HitReader typeclass, aggregation result types
- [pagination.md](pagination.md) — scroll API, search-after, point-in-time
- [examples.md](examples.md) — complete working examples for common use cases
- [index-management.md](index-management.md) — createIndex, field types, putMapping, indexInto, updateById, deleteById, deleteByQuery, RefreshPolicy, Indexable typeclass
- [debugging.md](debugging.md) — explain, validateIn, analyze, profile, trackTotalHits, termVectors, CAT APIs, dumping query JSON
