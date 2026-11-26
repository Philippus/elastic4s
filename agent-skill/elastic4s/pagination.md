# Pagination Reference

---

## From / Size (basic pagination)

```scala
search("index")
  .query(matchAllQuery())
  .from(0).size(20)    // page 1
  .from(20).size(20)   // page 2

// Aliases:
search("index").start(0).limit(20)
```

Default: `from = 0`, `size = 10`.

**Limit**: Elasticsearch rejects requests where `from + size > index.max_result_window` (default 10 000). For deeper pagination use search-after or PIT.

---

## Scroll API (stateful, batch export)

The scroll API is designed for batch export of large result sets. It is **not** intended for real-time user pagination — use search-after for that.

### Step 1 — initial search

```scala
import scala.concurrent.duration._

val resp1 = client.execute {
  search("index")
    .query(matchAllQuery())
    .sortBy(fieldSort("date").desc())
    .size(100)
    .scroll("1m")        // or .keepAlive("1m") or .keepAlive(1.minute)
}.await.result

val scrollId: String = resp1.scrollId.get
val page1: Array[SearchHit] = resp1.hits.hits
```

The first request opens a scroll context on the cluster. The `scroll` parameter sets how long the context is kept alive between requests (it is refreshed on each continuation).

### Step 2 — continuation

```scala
val resp2 = client.execute {
  searchScroll(scrollId).keepAlive("1m")
  // or with FiniteDuration:
  searchScroll(scrollId).keepAlive(1.minute)
}.await.result

val nextScrollId: String = resp2.scrollId.get  // always use the latest scrollId
val page2: Array[SearchHit] = resp2.hits.hits
```

Continue until `resp.hits.hits.isEmpty` — that indicates all documents have been consumed.

### Step 3 — close the scroll context

Release resources as soon as you are done. Not closing scroll contexts wastes memory on the cluster.

```scala
client.execute {
  clearScroll(scrollId)
}.await

// Multiple scroll IDs at once:
client.execute {
  clearScroll(scrollId1, scrollId2)
}.await

// Check result:
val cleared = client.execute(clearScroll(scrollId)).await.result
cleared.succeeded   // Boolean
cleared.num_freed   // Int — number of contexts freed
```

### Full loop pattern

```scala
var scrollId = client.execute {
  search("index").matchAllQuery().size(500).scroll("5m")
}.await.result.scrollId.get

var continue = true
while (continue) {
  val resp = client.execute {
    searchScroll(scrollId).keepAlive("5m")
  }.await.result

  if (resp.hits.isEmpty) {
    continue = false
    client.execute(clearScroll(scrollId)).await
  } else {
    scrollId = resp.scrollId.get
    resp.hits.hits.foreach(processHit)
  }
}
```

### SearchIterator — high-level blocking iterator

`SearchIterator` wraps the scroll loop in a standard `Iterator`. Each call to `hasNext` or `next()` blocks until the next page is fetched. Only usable with `ElasticClient[Future]`.

```scala
import com.sksamuel.elastic4s.requests.searches.SearchIterator
import scala.concurrent.duration._

implicit val timeout: FiniteDuration = 30.seconds

// Iterator[SearchHit]:
val hits: Iterator[SearchHit] = SearchIterator.hits(
  client,
  search("index")
    .query(matchAllQuery())
    .sortBy(fieldSort("name").asc())
    .size(100)
    .scroll("1m")
)
hits.foreach(hit => println(hit.sourceAsString))

// Iterator[T] (requires HitReader[T] in scope):
val articles: Iterator[Article] = SearchIterator.iterate[Article](
  client,
  search("index")
    .query(matchAllQuery())
    .size(200)
    .scroll("2m")
)
articles.toList
```

`SearchIterator` automatically clears the scroll when the iterator is exhausted. Abandoning a partially-consumed iterator leaks the scroll context.

### Parallel scroll with slice

Split the scroll across N independent consumers, each processing a disjoint partition:

```scala
// Consumer 0 of 4:
val req0 = search("index")
  .query(matchAllQuery())
  .size(500)
  .scroll("5m")
  .slice(id = 0, max = 4)

// Consumer 1 of 4:
val req1 = search("index")
  .query(matchAllQuery())
  .size(500)
  .scroll("5m")
  .slice(id = 1, max = 4)

// ... run each consumer independently, each with its own scrollId
```

Each consumer has its own `scrollId` and must be closed independently. Typical use: parallel Akka/ZIO streams processing.

---

## Search After (stateless deep pagination)

The recommended approach for user-facing pagination beyond the 10 000-document limit. Stateless — no server-side context is held.

**Requirements:**
- A stable sort with a unique tiebreaker (typically `_id` or a dedicated unique field)
- The sort must be identical on every page request

### Basic pattern

```scala
// First page — no searchAfter
val resp1 = client.execute {
  search("index")
    .query(matchAllQuery())
    .sortBy(
      fieldSort("date").desc(),
      fieldSort("_id").asc()    // unique tiebreaker
    )
    .size(10)
}.await.result

// Extract sort values from the last hit
val lastHit    = resp1.hits.hits.last
val sortValues = lastHit.sort.get   // Seq[AnyRef]

// Next page — pass sortValues
val resp2 = client.execute {
  search("index")
    .query(matchAllQuery())
    .sortBy(
      fieldSort("date").desc(),
      fieldSort("_id").asc()
    )
    .size(10)
    .searchAfter(sortValues)
}.await.result
```

Keep passing `resp.hits.hits.last.sort.get` from each page to the next `.searchAfter(...)` call. Stop when `resp.hits.hits.isEmpty`.

### Combining with PIT for consistent results

Without a PIT, concurrent index writes can cause documents to appear or disappear between pages. Using a PIT freezes the index view:

```scala
import scala.concurrent.duration._

// Open a PIT
val pitId = client.execute {
  createPointInTime("index").keepAlive(5.minutes)
}.await.result.id

// First page with PIT
val resp1 = client.execute {
  search("index")
    .pit(Pit(pitId, keepAlive = Some(5.minutes)))
    .sortBy(fieldSort("_shard_doc").asc())   // implicit sort on PIT
    .size(10)
}.await.result

val sortValues = resp1.hits.hits.last.sort.get

// Next page
val resp2 = client.execute {
  search("index")
    .pit(Pit(pitId, keepAlive = Some(5.minutes)))
    .sortBy(fieldSort("_shard_doc").asc())
    .size(10)
    .searchAfter(sortValues)
}.await.result

// When done, close the PIT
client.execute { deletePointInTime(pitId) }.await
```

When using a PIT, the index name in `search(...)` is ignored — the PIT determines which index snapshot to use.

---

## Point In Time (PIT)

A PIT creates a lightweight snapshot of the index state. It can be used standalone with search-after (above) or to paginate aggregations.

### Lifecycle

```scala
import scala.concurrent.duration._
import com.sksamuel.elastic4s.requests.pit.{CreatePitResponse, DeletePitResponse}

// Open
val pitResp: CreatePitResponse = client.execute {
  createPointInTime("my-index").keepAlive(5.minutes)
}.await.result

val pitId: String = pitResp.id

// Use in searches (see search-after section above)
search("my-index").pit(Pit(pitId, keepAlive = Some(5.minutes)))

// Close
val closeResp: DeletePitResponse = client.execute {
  deletePointInTime(pitId)
}.await.result

closeResp.succeeded   // Boolean
closeResp.num_freed   // Int
```

`keepAlive` on `createPointInTime` sets the initial TTL. `keepAlive` on the `Pit` in the search request refreshes it on each use. Always close a PIT when done to free cluster resources.

### PIT with implicit `_shard_doc` sort

When using a PIT, Elasticsearch adds `_shard_doc` as an implicit tiebreaker. You can make it explicit for full control:

```scala
search("index")
  .pit(Pit(pitId, keepAlive = Some(1.minute)))
  .sortBy(
    fieldSort("date").desc(),
    fieldSort("_shard_doc").asc()   // explicit tiebreaker injected by PIT
  )
  .size(100)
```

---

## Choosing the right strategy

| Strategy | When to use | Limitations |
|---|---|---|
| `from` / `size` | Small datasets, simple page navigation | Inefficient past ~10 000 docs (`max_result_window`) |
| Scroll | Bulk export, background processing | Stateful (cluster memory), not for real-time use |
| `SearchIterator` | Simple blocking iteration over all docs | `Future`-only, blocks on each page |
| Search After | User-facing deep pagination | Requires stable sort + unique tiebreaker; can't jump to arbitrary pages |
| Search After + PIT | Consistent deep pagination on live index | PIT consumes cluster resources; must be closed |
