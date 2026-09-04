# elastic4s — Debugging

## Imports

```scala
import com.sksamuel.elastic4s.ElasticDsl._
```

---

## Explain API — why did a document score this way?

```scala
// Explain why document "42" matched (or didn't) the given query
val resp = client.execute {
  explain("my-index", "42") query matchQuery("name", "elasticsearch")
}.await.result

resp.matched           // Boolean
resp.isMatch           // alias for matched
resp.explanation       // Explanation(value, description, details)
resp.ref               // DocumentRef(index, id)

// Recursive explanation tree
def printExplanation(e: Explanation, indent: Int = 0): Unit = {
  println(" " * indent + s"${e.value}: ${e.description}")
  e.details.foreach(printExplanation(_, indent + 2))
}
printExplanation(resp.explanation)
```

### Options

```scala
explain("my-index", "42")
  .query(boolQuery().must(matchQuery("name", "foo")).filter(termQuery("status", "active")))
  .fetchSource(true)      // include _source in response
  .routing("shard-key")
  .preference("_local")
  .lenient(true)
```

---

## Validate API — is the query valid?

```scala
val resp = client.execute {
  validateIn("my-index") query matchQuery("name", "elasticsearch")
}.await.result

resp.valid      // Boolean
resp.isValid    // alias
resp.explanations  // Seq[Explanation(index, valid, error)]

// With query rewrite info
client.execute {
  validateIn("my-index")
    .query(wildcardQuery("name", "elast*"))
    .rewrite(true)    // show rewritten query
    .explain(true)
}
```

---

## Analyze API — how does the analyzer tokenize text?

```scala
// Standard analyzer on free text
val resp = client.execute {
  analyze("hello world").analyzer("standard")
}.await.result

resp.tokens.foreach { t =>
  println(s"${t.token} [${t.startOffset}-${t.endOffset}] type=${t.`type`}")
}

// Field mapping analyzer (uses the analyzer configured for that field)
client.execute {
  analyze("hello world")
    .index("my-index")
    .field("name")
}

// Custom tokenizer + filters
client.execute {
  analyze("Hello World!")
    .tokenizer("standard")
    .filters("lowercase", "asciifolding")
}

// Detailed token info
client.execute {
  analyze("hello world")
    .analyzer("standard")
    .explain(true)
    .attributes("bytes", "positionLength")
}
```

---

## Explain score in a search (per-hit)

```scala
val resp = client.execute {
  search("my-index")
    .query(matchQuery("name", "elasticsearch"))
    .explain(true)          // add _explanation to every hit
}.await.result

resp.hits.hits.foreach { hit =>
  println(hit._id)
  hit.explanation.foreach(e => println(s"  score=${e.value}: ${e.description}"))
}
```

---

## Profile API — where does query time go?

```scala
val resp = client.execute {
  search("my-index")
    .query(boolQuery()
      .must(matchQuery("name", "elasticsearch"))
      .filter(termQuery("status", "active"))
    )
    .profile(true)    // enable profiling
}.await.result

resp.profile.foreach { profile =>
  profile.shards.foreach { shard =>
    println(s"Shard: ${shard.id}")
    shard.searches.foreach { s =>
      s.query.foreach { q =>
        println(s"  Query type: ${q.`type`}, time: ${q.timeInNanos}ns")
      }
    }
  }
}
```

---

## Track total hits

```scala
search("my-index")
  .query(matchAllQuery())
  .trackTotalHits(true)    // always return exact count (not capped at 10 000)

// In response:
resp.hits.total.value      // Long — exact count
resp.hits.total.relation   // "eq" (exact) | "gte" (lower bound)
```

---

## Track scores (when sorting by field)

```scala
search("my-index")
  .query(matchQuery("name", "elastic"))
  .sortBy(fieldSort("price").asc())
  .trackScores(true)    // compute _score even when not sorting by relevance
```

---

## Include version / seq_no in hits

```scala
search("my-index")
  .query(matchAllQuery())
  .version(true)              // include _version in each hit
  .seqNoPrimaryTerm(true)    // include _seq_no and _primary_term
```

---

## Term Vectors — analyze a document's field statistics

```scala
val resp = client.execute {
  termVectors("my-index", "42")
    .fields("name", "description")
    .termStatistics(true)    // df, ttf
    .fieldStatistics(true)   // doc_count, sum_doc_freq, sum_ttf
    .offsets(true)
    .positions(true)
}.await.result

resp.termVectors.foreach { case (field, tv) =>
  tv.terms.foreach { case (term, info) =>
    println(s"$field.$term: tf=${info.termFreq}")
  }
}
```

---

## CAT APIs — cluster and index diagnostics

```scala
// Index health + document count + store size
client.execute { catIndices() }.await.result.foreach { idx =>
  println(s"${idx.index}: health=${idx.health}, docs=${idx.count}, size=${idx.storeSize}")
}

// Filter by health
client.execute { catIndices(HealthStatus.Red) }

// Shard allocation
client.execute { catShards() }.await.result.foreach { s =>
  println(s"${s.index}[${s.shard}] ${s.prirep}: ${s.state} on ${s.node}")
}

// Node stats
client.execute { catNodes() }.await.result.foreach { n =>
  println(s"${n.name}: cpu=${n.cpu}%, heap=${n.heapPercent}%")
}

// Aliases
client.execute { catAliases() }
client.execute { catAliases("products-*") }

// Document count for an index
client.execute { catCount("my-index") }

// Master node info
client.execute { catMaster() }

// Segment details
client.execute { catSegments(Indexes("my-index")) }

// Thread pool saturation
client.execute { catThreadPool() }
```

---

## Dump the HTTP request body (query as JSON)

elastic4s uses a `Show[ElasticRequest]` typeclass to serialize the full HTTP request for debugging.

```scala
import com.sksamuel.elastic4s.handlers.searches.SearchHandler
import com.sksamuel.elastic4s.Show

val req = search("my-index")
  .query(boolQuery().must(matchQuery("name", "elastic")).filter(termQuery("status", "active")))
  .size(10)
  .aggs(termsAgg("by_cat", "category"))

// Build the raw HTTP request and print it
val elasticRequest = SearchHandler.build(req)
println(Show[ElasticRequest].show(elasticRequest))
// → GET /my-index/_search
//   {"query":{"bool":{"must":[...],"filter":[...]}},"size":10,"aggs":{...}}
```

`ElasticRequest` fields:
```scala
elasticRequest.method    // "GET" | "POST" | "PUT" | "DELETE"
elasticRequest.endpoint  // "/my-index/_search"
elasticRequest.params    // Map[String, String] — query string params
elasticRequest.entity    // Option[HttpEntity] — request body
```

---

## Get Mapping — inspect the current mapping

```scala
// Full mapping for an index
val resp = client.execute { getMapping("my-index") }.await.result

// Specific fields
client.execute { getMapping(Indexes("my-index"), "name", "price") }
```

---

## Quick debugging checklist

| Symptom | Tool |
|---------|------|
| Wrong relevance score | `explain("index","id").query(q)` |
| Query not returning expected docs | `validateIn("index").query(q).explain(true)` |
| Tokenization / analyzer behaviour | `analyze("text").analyzer("name").index("idx")` |
| Query slow | `search(...).profile(true)` |
| Hit count wrong | `.trackTotalHits(true)` |
| Wrong shard routing | `catShards()` |
| Index red / yellow | `catIndices()` + `catShards()` |
| See the actual JSON sent | `Show[ElasticRequest].show(handler.build(req))` |
