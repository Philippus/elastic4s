## Updates

An [update request](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html) allows us to update a document based on either a provided field set or a script.
The operation loads the document, applies the script or merges the given fields, and re-indexes.

To update a document by id we use the `updateById` method.

```scala
client.execute {
  updateById("scifi", "4").doc(
    "name" -> "spock",
    "race" -> "vulcan"
  )
}
```

Which will update the document with id 4 with the new fields.

Update is [bulk](bulk.md) compatible so we can issue multiple requests at once

```scala
client.bulk {
  updateById("scifi", "5").doc("name" -> "spock"),
  updateById("scifi", "8").doc("name" -> "kirk"),
  updateById("scifi", "9").doc("name" -> "scottie")
}
```

If the document does not exist then an error will be returned. However, sometimes
you might want to insert the document if it does not already exist, if so, you can use the upsert
operation.

```scala
client.execute {
  updateById("scifi", "25").docAsUpsert(
    "character" -> "chewie",
    "race" -> "wookie"
  )
}
```

If document 25 does not exist then it will be created with the supplied fields.

Update also supports updating via a script, eg

```scala
client.execute {
  updateById("scifi", "5").script("ctx._source.birthplace = 'iowa'")
}
```

Now document 5 will had have its field `birthplace` set to `iowa`, which is of course Captain Kirk's birthplace.

If you want to do a script update with params then we need make a script instance and pass that to the update request.

```scala
client.sync.execute {
  updateById("scifi", "98").script(
    script("ctx._source.tags += tag").params(Map("tag"->"space"))
  }
}
````

Like index, you can use update with explicit field values.

```scala
client.execute {
  updateById("scifi", "14").docAsUpsert(
    NestedFieldValue("captain", Seq(SimpleFieldValue("james", "kirk")))
  )
}
````

### Update by Query

We can perform an [update by query](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update-by-query.html) as well as update by id.

The _update-by-query_ query requires a script.

```scala
client.execute {
  updateByQuery("bands", term("type", "pop")).script(script("ctx._source.foo = 'a'").lang("painless"))
}
```

A common use case of _update-by-query_ is to have a set of documents be refreshed to pick up changes in mappings by running the query without any changes.

```scala
client.execute {
  updateByQuery("pop", matchAllQuery())
}
```
