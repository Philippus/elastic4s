# elastic4s — Index Management

## Imports

```scala
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexRequest, IndexRequest}
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.elastic4s.fields._
```

---

## Create Index

```scala
// Minimal
client.execute { createIndex("my-index") }

// With shards / replicas / refresh interval
client.execute {
  createIndex("my-index")
    .shards(3)
    .replicas(1)
    .refreshInterval("5s")          // or .refreshInterval(5.seconds)
}

// With mapping + settings
client.execute {
  createIndex("products")
    .shards(2)
    .replicas(1)
    .mapping(
      properties(
        keywordField("id"),
        textField("name").analyzer("english").boost(4),
        textField("description"),
        keywordField("category"),
        doubleField("price"),
        dateField("created_at").format("strict_date_optional_time"),
        nestedField("tags").fields(
          keywordField("value")
        ),
        objectField("meta").fields(
          keywordField("source"),
          intField("weight")
        )
      )
    )
}

// With aliases
client.execute {
  createIndex("products-v2")
    .alias("products")              // simple alias
    .alias("products-active", termQuery("active", true))  // filtered alias
}

// Raw JSON source
client.execute {
  createIndex("my-index").source("""{"settings":{"number_of_shards":1}}""")
}
```

### Response
```scala
case RequestSuccess(_, _, _, resp: CreateIndexResponse) =>
  resp.index      // String — index name
  resp.acknowledged   // Boolean
  resp.shardsAcknowledged // Boolean
```

---

## Field Types

```scala
// Text (full-text search)
textField("name")
  .analyzer("english")
  .searchAnalyzer("standard")
  .boost(2.0)
  .fields(keywordField("raw"))    // sub-field for aggregations
  .stored(true)
  .index(true)

// Keyword (exact match, aggregation, sort)
keywordField("status")
  .normalizer("lowercase")
  .ignoreAbove(256)
  .docValues(true)

// Numeric
intField("count")
longField("timestamp_ms")
floatField("score")
doubleField("price")
shortField("code")
byteField("flag")

// Date
dateField("created_at").format("strict_date_optional_time||epoch_millis")

// Boolean
booleanField("active")

// Binary
binaryField("attachment")

// Geo
geoPointField("location")
geoShapeField("area")

// Object / Nested
objectField("address").fields(
  textField("street"),
  keywordField("city"),
  keywordField("country")
)
nestedField("comments").fields(
  keywordField("author"),
  textField("body")
)

// Join (parent-child)
joinField("relation").relations("question" -> "answer")

// Rank features
rankFeatureField("pagerank")
rankFeaturesField("keywords")

// Dense vector (for kNN)
denseVectorField("embedding").dims(768)

// Wildcard
wildcardField("log_message")
```

---

## Update Mapping (putMapping)

```scala
client.execute {
  putMapping("my-index").as(
    keywordField("new_field"),
    textField("another_field").analyzer("standard")
  )
}

// Dynamic mapping control
client.execute {
  putMapping("my-index")
    .dynamic(DynamicMapping.Strict)   // Strict / True / False / Runtime
    .source(enabled = true)
    .as(keywordField("tag"))
}
```

### Get Mapping

```scala
// Full index mapping
client.execute { getMapping("my-index") }

// Specific fields
client.execute { getMapping(Indexes("my-index"), "name", "price") }

// Response
case RequestSuccess(_, _, _, resp) =>
  // resp is GetMappingResponse — iterate resp.mappings
```

---

## Index Settings

```scala
// Update dynamic settings on existing index
client.execute {
  updateSettings("my-index")
    .indexSetting("index.number_of_replicas", 2)
    .indexSetting("index.refresh_interval", "30s")
}

// Get settings
client.execute { getSettings(Indexes("my-index")) }
```

---

## Index a Document

```scala
// From field tuples (auto-generated ID)
client.execute {
  indexInto("products").fields(
    "name"     -> "Laptop",
    "price"    -> 999.99,
    "category" -> "electronics"
  )
}

// With explicit ID
client.execute {
  indexInto("products").id("SKU-001").fields(
    "name"  -> "Laptop",
    "price" -> 999.99
  )
}

// From JSON string
client.execute {
  indexInto("products").id("SKU-001").source("""{"name":"Laptop","price":999.99}""")
}

// From domain object (requires Indexable[T])
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._  // or circe/play

case class Product(name: String, price: Double)
client.execute {
  indexInto("products").id("SKU-001").doc(Product("Laptop", 999.99))
}

// Create-only (fail if ID already exists)
client.execute {
  indexInto("products").id("SKU-001").createOnly(true).doc(product)
}

// With refresh / routing / pipeline
client.execute {
  indexInto("products")
    .id("SKU-001")
    .doc(product)
    .refresh(RefreshPolicy.Immediate)
    .routing("shard-key")
    .pipeline("enrich-pipeline")
}
```

### IndexResponse fields
```scala
case RequestSuccess(_, _, _, resp: IndexResponse) =>
  resp.id             // String — document ID
  resp.index          // String — index name
  resp.version        // Long
  resp.seqNo          // Long
  resp.primaryTerm    // Long
  resp.result         // String: "created" | "updated"
  resp.shards         // Shards
```

---

## Update a Document

### Partial update by ID

```scala
// Update specific fields
client.execute {
  updateById("products", "SKU-001").doc(
    "price" -> 899.99,
    "on_sale" -> true
  )
}

// Update from JSON
client.execute {
  updateById("products", "SKU-001").doc("""{"price":899.99}""")
}

// Update from domain object
client.execute {
  updateById("products", "SKU-001").doc(updatedProduct)
}

// Upsert (create if not exists, update if exists)
client.execute {
  updateById("products", "SKU-001")
    .doc("price" -> 899.99)
    .upsert("name" -> "Laptop", "price" -> 899.99)
}

// docAsUpsert: use the doc body for both update and insert
client.execute {
  updateById("products", "SKU-001")
    .docAsUpsert("name" -> "Laptop", "price" -> 999.99)
}

// Script update
client.execute {
  updateById("products", "SKU-001").script(
    Script("ctx._source.price -= params.discount").params(Map("discount" -> 50))
  )
}

// With scripted upsert
client.execute {
  updateById("products", "SKU-001")
    .script(Script("ctx._source.views = (ctx._source.views ?: 0) + 1"))
    .scriptedUpsert(true)
    .upsert("views" -> 0)
}

// Optimistic concurrency control
client.execute {
  updateById("products", "SKU-001")
    .doc("price" -> 899.99)
    .ifSeqNo(42L)
    .ifPrimaryTerm(1L)
}

// Options
.retryOnConflict(3)
.detectNoop(false)        // default true: skip if nothing changed
.refresh(RefreshPolicy.Immediate)
.routing("shard-key")
.fetchSource(true)        // return updated source in response
```

### Update by query

```scala
client.execute {
  updateByQuerySync("products", termQuery("category", "electronics"))
    .script(Script("ctx._source.discounted = true"))
    .refresh(RefreshPolicy.Immediate)
    .proceedOnConflicts(true)
}
```

---

## Delete a Document

```scala
// By ID
client.execute { deleteById("products", "SKU-001") }

// With options
client.execute {
  deleteById("products", "SKU-001")
    .refresh(RefreshPolicy.Immediate)
    .routing("shard-key")
    .ifSeqNo(42L)
    .ifPrimaryTerm(1L)
}

// By query
client.execute {
  deleteByQuery("products", termQuery("active", false))
    .refresh(RefreshPolicy.Immediate)
    .proceedOnConflicts(true)
    .scrollSize(500)
    .maxDocs(10000)
}
```

### DeleteResponse fields
```scala
case RequestSuccess(_, _, _, resp: DeleteResponse) =>
  resp.id       // String
  resp.result   // String: "deleted" | "not_found"
  resp.version  // Long
  resp.shards   // Shards
```

---

## Delete Index

```scala
client.execute { deleteIndex("my-index") }
client.execute { deleteIndex("index-a", "index-b") }

case RequestSuccess(_, _, _, resp: DeleteIndexResponse) =>
  resp.acknowledged   // Boolean
```

---

## Index Existence / Get Index

```scala
client.execute { indexExists("my-index") }

case RequestSuccess(_, _, _, resp: IndexExistsResponse) =>
  resp.isExists   // Boolean

client.execute { getIndex("my-index") }
```

---

## Refresh Policy

```scala
RefreshPolicy.None        // default: async refresh
RefreshPolicy.Immediate   // wait for refresh before returning (slow)
RefreshPolicy.WaitFor     // wait for next scheduled refresh
```

---

## Indexable Typeclass

Needed for `.doc(obj)` on `IndexRequest` / `UpdateRequest`.

```scala
// Jackson (auto — add to scope)
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

// Circe (auto-derive case classes)
import io.circe.generic.auto._
import com.sksamuel.elastic4s.circe._

// Manual
import com.sksamuel.elastic4s.Indexable

implicit val productIndexable: Indexable[Product] = (t: Product) =>
  s"""{"name":"${t.name}","price":${t.price}}"""
```
