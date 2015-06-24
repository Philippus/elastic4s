## Multi Get

The multiget request allows us to execute multiple get requests in a single request, reducing round trip latency.
The format is simple, pass a list of get requests into the client method.

Then to issue multiple get requests we can do something like the following:

```scala
val resp = client.execute(
  multiget(
    get id 3 from "coldplay/albums",
    get id 5 from "coldplay/albums",
    get id 7 from "coldplay/albums"
  )
)
```

This is exactly the same as for the get request, except you must wrap the multiple get requests inside a multiget
block. Routing, version and fetched fields options can be specified in the same way as normal get requests:

```scala
val resp = client.execute(
  multiget(
    get id 3 from "coldplay/albums" routing "2" fields("name", "year"),
    get id 5 from "coldplay/albums" routing "1",
    get id 7 from "coldplay/albums" version 5
  )
)
```
