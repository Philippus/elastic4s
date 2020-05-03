## Bulk Operations

Elasticsearch allows us to index, delete and update in bulk mode for much faster throughput. When using the bulk API
you will save at the very least the latency of multiple requests, and usually Elasticsearch can optimize when it
knows it will be doing multiple requests in the same index.

Elastic4s supports bulk operations in an easy way. Index, update and delete operations use the same syntax as before
except they are now wrapped in a `bulk` keyword.

For example, bulk index requests:

```scala
val resp = client.execute {
  bulk(
    indexInto("bands").fields("name" -> "coldplay"),
    indexInto("bands").fields("name" -> "kings of leon"),
    indexInto("bands").fields("name" -> "elton john", "best_album" -> "tumbleweed connection")
  )
}
```

An example of bulk delete operations:

```scala
val resp = client.execute {
  bulk(
    deleteById("places", "3"),
    deleteById("artists", "2"),
    deleteById("places", "4"),
  )
}
```

The bulk API supports combining different type of operations: updates, inserts, deletes.

```scala
val resp = client.execute {
  bulk(
    indexInto("bands").fields("name" -> "coldplay"),
    indexInto("bands").fields("name" -> "kings of leon"),
    deleteById("places", "3"),
    deleteById("artists", "2"),
    updateById("bands", "4").doc("name" -> "kate bush")
  )
}
```

### Responses

The response object contains how many successes and failures.

```scala
val f = client.execute {
  bulk(
    indexInto("bands").fields("name" -> "coldplay"),
    indexInto("bands").fields("name" -> "kings of leon"),
    deleteById("places", "3"),
    deleteById("artists", "2"),
    updateById("bands", "4").doc("name" -> "kate bush")
  )
}
f.map { resp =>
  println("Failures: ${resp.failures}")
  println("Successes: ${resp.successes}")
}
```
