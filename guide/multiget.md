## Multi Get

The multiget request allows us to execute multiple get requests in a single request, reducing round trip latency.
The format is simple, pass a list of get requests into the client method.

Lets create an index (using the bulk API to insert multiple documents at once) that contains 4 documents

```scala
  import com.sksamuel.elastic4s.ElasticClient
  import com.sksamuel.elastic4s.ElasticDsl._

  client bulk (
    index into "coldplay/albums" id 1 fields "name" -> "mylo xyloto", // note the trailing commas
    index into "coldplay/albums" id 2 fields "name" -> "x & y", // we are invoking a var args method
    index into "coldplay/albums" id 3 fields "name" -> "parachutes",
    index into "coldplay/albums" id 4 fields "name" -> "viva la vida"
    )
```

Then to issue multiple get requests we can do something like

```scala
  val resp = client.sync.get (
    2 from "coldplay/albums", // note the trailing comma, we are invoking a var args method
    4 from "coldplay/albums"
  )
```

The resp value is of type MultiGetResponse, the standard Java API response. This response object would have 2 results,
the first being id 2 (X&Y) and the second being id 4 (Viva La Vida)

Note, the scala client has no distinction in the syntax between multiget and single get.
You simply choose to either invoke with a single get request and get back a GetResponse or invoke with many gets and get back a MultiGetResponse.

```scala
  // invoke for single get
  val resp1 = client.sync.get (
    2 from "coldplay/albums"
  )
  
  // invoke for multi get
  val resp2 = client.sync.get (
    2 from "coldplay/albums", // note the trailing comma, we are invoking a var args method
    4 from "coldplay/albums"
  )
```

If you invoke a get request for an unknown id then the search response will contain a Response object that returns false for isExists().

```scala
  val resp = client.sync.get (
    55 from "coldplay/albums"
  )

  resp.getResponses.toSeq(0).getResponse.isExists == false
```