## Multi Get

The multiget request allows us to execute multiple get requests in a single request, reducing round trip latency.
The format is simple, pass a list of get requests into the client method.

Then to issue multiple get requests we can do something like the following.

```scala
val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums",
        get id 5 from "coldplay/albums",
        get id 34 from "coldplay/albums"
      )
    )
```

This is exactly the same as for the get request, except you must wrap the multiple get requests inside a multiget
block. That multiget block can be used to set the other parameters too, such as routing, preference, etc.