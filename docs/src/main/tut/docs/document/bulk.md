---
layout: docs
title:  "Bulk API"
section: "docs"
---

# Bulk Operations

Elasticsearch allows us to index, delete and update in bulk mode for much faster throughput. When using the bulk API
you will save at the very least the latency of multiple requests, and usually Elasticsearch can optimize when it
knows it will be doing multiple requests in the same index/type.

Elastic4s supports bulk operations in an easy way. Index, update and delete operations use the same syntax as before
except they are now wrapped in a `bulk` keyword.

For example, bulk indexes:

```scala
val resp = client.execute {
  bulk (
    index into "bands/rock" fields "name"->"coldplay",
    index into "bands/rock" fields "name"->"kings of leon",
    index into "bands/pop" fields ("name"->"elton john", "best_album"->"goodbye yellow brick road")
  )
}
```

For those of you who are new to Scala, you will notice the bulk keyword uses parenthesis. This is required because
`bulk` accepts a Seq, where as `execute`, which can use parenthesis or braces accepts a single value (the bulk block).

An example of bulk delete operations:

```scala
val resp = client.execute {
  bulk (
    delete id 3 from "places/cities",
    delete id 8 from "places/cities",
    delete id 3 from "music/bands"
  )
}
```

The bulk API supports combining different type of operations:

```scala
val resp = client.execute {
  bulk (
    index into "bands/rock" fields "name"->"coldplay",
    index into "bands/rock" fields "name"->"kings of leon",
    delete id 3 from "places/cities",
    delete id 8 from "places/cities"
  )
}
```
