## Alias

An [index alias](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html) is a logical name used to reference one or more indices. Most Elasticsearch APIs accept an index alias in place of an index name.

### Add and Remove

To add and remove aliases using elastic4s, we create instances of `AliasAction` and pass those to an `AliasRequest` which we can create manually or by the DSL method `aliases`.

For example, to add a single alias to the `beach` index.

```scala
client.execute {
  aliases(
    addAlias("beaches_alias", "beach")
  )
}
```

We can perform multiple alias actions in a single request, including removal.

```scala
client.execute {
  aliases(
    removeAlias("beaches_alias", "beach"),
    addAlias("mountains_alias", "mountains")
  )
}
```

### Filters

Aliases can also include [filters](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html#filtered).

```scala
client.execute {
  aliases(
    addAlias("metal_beaches", "beaches").filter(prefixQuery("name", "g"))
  )
}
```

### Exists

To check if an alias exists, we can use:

```scala
client.execute {
  aliasExists("mountains_alias")
}
```

### Get Aliases

We can retrieve aliases for a given index(es), or we can retrieve indexes for a given alias(es), or combinations therefore.

To lookup aliases used by one or more index:

```
client.execute {
  getAliases(Seq("mountains", "beaches"), Nil)
}
```

And to lookup by alias:

```
client.execute {
  getAliases(Nil, Seq("mountains_alias", "metal_beaches"))
}
```

