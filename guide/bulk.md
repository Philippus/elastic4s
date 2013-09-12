### Bulk Operations

Elasticsearch allows us to index, delete and update in bulk mode for much faster turnaround (saving a ton of round trip requests at the very least).

Elastic4s supports this is a very easy way. Simply use the index, update or delete syntax as before, except combine into a sequence and pass to the bulk method.

For example, multiple indexing:

```scala
client.bulk {
   index into "bands/rock" fields "name"->"coldplay",
   index into "bands/rock" fields "name"->"kings of leon",
   index into "bands/pop" fields (
      "name"->"elton john",
      "best_album"->"goodbye yellow brick road"
   )
}
```

And multiple deletin:g

```scala
  client.delete {
    "places/cities" -> 4,
    "places/cities" -> 1,
    "music/bands" -> 19
  }```

And we can even combine all the different types into a single bulk request:

```scala
client.bulk {
   index into "bands/rock" fields "name"->"coldplay",
   index into "bands/rock" fields "name"->"kings of leon",
   delete {
     "places" types "cities" where "name:london"
   },
   delete {
      "places" types "cities" where "name:london"
   }
}
```