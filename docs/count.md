## Count

A [count request](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-count.html) executes a query and returns a count of the number of matching documents for that query.
It can be executed across one or more indices. The query can be omitted for a total count across the indexes.

To count all documents in an index regardless of type, we can do this.

```scala
client.execute {
  count("places")
}
```

We can do multiple indexes at once.

```scala
client.execute {
  count(Seq("places", "movies"))
}
```

We can include a query to narrow down the results.

```scala
client.execute {
  count("places").query(termQuery("borough", "westminster"))
}
```

There are multiple options on count, such as _expand wilcards_, _ignore throttled_, and so on.
See the [official docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-count.html) for a full list.
