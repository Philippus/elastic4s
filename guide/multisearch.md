#### Multi Search

The multisearch request type allows us to execute multiple searches in the a single request.
The format is simple, pass a list of search requests into the client method.

Lets create an index (using the bulk API to insert multiple documents at once) that contains 4 documents

```scala
  import com.sksamuel.elastic4s.ElasticClient
  import com.sksamuel.elastic4s.ElasticDsl._

  client execute (
    index into "coldplay/albums" id 1 fields "name" -> "mylo xyloto", // note the trailing commas
    index into "coldplay/albums" id 2 fields "name" -> "x & y", // we are invoking a var args method
    index into "coldplay/albums" id 3 fields "name" -> "parachutes",
    index into "coldplay/albums" id 4 fields "name" -> "viva la vida"
    )
```

Then to issue multiple search requests at once, we use

```scala
  val resp = client.sync.execute(
    search in "jtull" query "mylo", // note the trailing comma, we are invoking a var args method
    search in "jtull" query "viva"
  )
```

The resp value is of type MultiSearchResponse, the standard Java API response.

Note, the scala client has no distinction in the syntax between multisearch and standard single search.
You simply choose to either invoke with a single search and get back a SearchResponse or invoke with many searches and get back a MultiSearchResponse.