---
layout: docs
title:  "Alias API"
section: "docs"
---

# Aliases

The add alias request allows us to alias an existing index to another name:

```scala
val resp = client.execute {
  addAlias("places") on "locations"
}
```

Aliases can include filters to assist with partitioning data (such as limiting results to a certain customer or project) or commonly applied filtering:

```scala
val resp = client.execute {
  addAlias("uk-locations") on "locations" filter termFilter("country", "uk")
}
```

Existing aliases can be removed using a similar request:

```scala
val resp = client.execute {
  removeAlias("places") on "locations"
}
```

Multiple operations on aliases can be executed atomically:

```scala
val resp = client.execute {
  aliases(
    removeAlias("places") on "old_locations",
    addAlias("places") on "new_locations"
  )
}
```


For more information on the options for aliases, consult the official ElasticSearch [docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-aliases.html).
