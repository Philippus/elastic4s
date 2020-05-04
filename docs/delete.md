## Deleting

A [delete request](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html) allows us to delete document(s) from an index based on an id or query.

For example, to delete a single document by id in an index called `places`:

```scala
client.execute {
  deleteById("places", "3")
}
```

We can take this a step further by deleting using a query rather than directly by id.
In this example we're deleting all bands where their type is pop.

```scala
client.execute {
  deleteByQuery("bands", termQuery("type", "pop"))
}
```

### Bulk

Delete is [bulk](bulk.md) compatible so we can issue multiple requests at once:

```scala
client.bulk {
  deleteById("places", "4")
  deleteById("places", "5")
  deleteById("places", "6")
}
```


### Index

To delete an entire index you can use `deleteIndex`:

```scala
client.execute {
  deleteIndex("places")
}
```

Or do delete all indices (careful!):

```scala
client.execute {
  deleteIndex("_all")
}
```
