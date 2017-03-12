---
layout: docs
title:  "Update API"
section: "docs"
---

# Updates

An update request allows us to update a document based on either a provided field set or a script.
The operation loads the document, applies the script or merges the given fields, and re-indexes.
Read [official update docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-update.html).

To update a document by id, we need to know the type and the index. Then we can issue a query such as

```scala
val resp = client.execute {
  update(5).in("scifi/startrek").doc(
    "name" -> "spock",
    "race" -> "vulcan"
  )
}
```

Which will update the document with id 5 with the new fields.

Update is bulk compatible so we can issue multiple requests at once

```scala
val resp = client.bulk {
  update(5).in("scifi/startrek").doc("name" -> "spock"),
  update(8).in("scifi/startrek").doc("name" -> "kirk"),
  update(9).in("scifi/startrek").doc("name" -> "scottie")
}
```

If the document does not exist then a DocumentMissingException will be thrown. However, sometimes
you might want to insert the document if it does not already exist, then you can use the upsert
operation. Eg,

```scala
val resp = client.execute {
  update id 25 in "scifi/starwars" docAsUpsert (
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
val resp = client.execute {
  update id 5 in "scifi/startrek" script "ctx._source.birthplace = 'iowa'"
}
```

Now document 5 will had have its field `birthplace` set to `iowa`, which is of course Captain Kirk's birthplace.

If you want to do a script update with params then you can do:

```scala
val resp = client.sync.execute {
  update id 98 in "scifi/battlestargalactica" script {
    script("ctx._source.tags += tag").params(Map("tag"->"space"))
  }
}
````

Like index, you can use update with explicit field values.

```scala
val resp = client.execute {
  update id 14 in "scifi/startrek" docAsUpsert (
    NestedFieldValue("captain", Seq(SimpleFieldValue("james", "kirk")))
  )
}
````
