## Updates

An update request allows us to update a document based on either a provided field set or a script.
The operation loads the document, applies the script or merges the given fields, and re-indexes.
Read [offical update docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-update.html).

To update a document by id, we need to know the type and the index. Then we can issue a query such as

```scala
client.execute {
  update(5).in("scifi/startrek").doc(
    "name" -> "spock",
    "race" -> "vulcan"
  )
}
```

Which will update the document with id 5 with the new fields.

Update is bulk compatible so we can issue multiple requests at once

```scala
client.bulk {
  update(5).in("scifi/startrek").doc("name" -> "spock"),
  update(8).in("scifi/startrek").doc("name" -> "kirk"),
  update(9).in("scifi/startrek").doc("name" -> "scottie")
}
```

If the document does not exist then a DocumentMissingException will be thrown. However, sometimes
you might want to insert the document if it does not already exist, then you can use the upsert
operation. Eg,

```scala
client.execute {
  update 25 in "scifi/starwars" docAsUpsert (
    "character" -> "chewie",
    "race" -> "wookie"
  )
}
```

If document 25 does not exist then it will be created with the supplied fields.
Also you'll notice there we switched to infix style, sometimes you might want to do that to make
your queries a little more readable but either works equally as well.

Update also supports updating via a script, eg

```scala
client.sync.execute {
  update id 5 in "scifi/startrek" script "ctx._source.birthplace = 'iowa'"
}
```

Now document 5 will had have its field `birthplace` set to `iowa`, which is of course Captain Kirk's birthplace.

If you want to do an upsert with script, then you can set the docAsUpsert option with true, eg:

```scala
client.sync.execute {
  update id 98 in "scifi/battlestargalactica" script "ctx._source.name = 'adama'" docAsUpsert true
}
```