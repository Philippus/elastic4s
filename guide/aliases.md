## Aliases

The add alias request allows us to alias an existing index to another name:

```scala
val resp = client.execute {
  addAlias("locations", "places")
}
```

Aliases can include filters to assist with partitioning data (such as limiting results to a certain customer or project) or commonly applied filtering:

```scala
val resp = client.execute {
  addAlias("locations", "uk-locations", FilterBuilders.termFilter("country", "uk"))
}
```

Existing aliases can be removed using a similar request:

```scala
val resp = client.execute {
  removeAlias("locations", "places")
}
```


For more information on the options for aliases, consult the official ElasticSearch [docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-aliases.html).
