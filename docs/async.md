## Async search

To execute an [async search](https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html) in elastic4s, you need to first create a search request and make it async. For example:

```scala
search("cities").query("London").async()
```

