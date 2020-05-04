## Multi Get

The multiget request allows us to execute multiple get requests in a single request, reducing round trip latency.
The format is simple, pass a list of [get](get.md) requests into the client method.

First, import the ElasticDSL

```scala
import com.sksamuel.elastic4s.ElasticDsl._
```

Then to issue multiple get requests we can do something like the following:

```scala
multiget(
  get("albums", 3),
  get("albums", 5),
  get("albums", 9)
)
```

This is exactly the same as for the get request, except you must wrap the multiple get requests inside a multiget
block. Routing, version and fetched fields options can be specified in the same way as normal get requests:

```scala
multiget(
  get("albums", 3).routing("2").storedFields("name", "year"),
  get("albums", 4),
  get("albums", 6).routing("2")
)
```
