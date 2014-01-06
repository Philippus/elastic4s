## Aliases

The add alias request allows us to alias an existing index to another name:

```scala
val resp = client.execute {
  aliases add "places" on "locations"
}
```

Aliases can include filters to assist with partitioning data (such as limiting results to a certain customer or project) or commonly applied filtering:

```scala
val resp = client.execute {
  aliases add "uk-locations" on "locations" filter FilterBuilders.termFilter("country", "uk") routing 4
}
```

Existing aliases can be removed using a similar request:

```scala
val resp = client.execute {
  aliases remove "places" on "locations"
}
```


For more information on the options for aliases, consult the official ElasticSearch [docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-aliases.html).
