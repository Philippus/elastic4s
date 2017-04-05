---
layout: docs
title:  "Multi Get API"
section: "docs"
---

# Multi Get

The multiget request allows us to execute multiple get requests in a single request, reducing round trip latency.
The format is simple, pass a list of get requests into the client method.

First, import the ElasticDSL

```tut:silent
import com.sksamuel.elastic4s.ElasticDsl._
```

Then to issue multiple get requests we can do something like the following:

```tut:book
multiget(
  get(3) from "coldplay/albums",
  get(5) from "coldplay/albums",
  get(7) from "coldplay/albums"
)
```

This is exactly the same as for the get request, except you must wrap the multiple get requests inside a multiget
block. Routing, version and fetched fields options can be specified in the same way as normal get requests:

```tut:book
multiget(
  get(3) from "coldplay/albums" routing "2" storedFields("name", "year"),
  get(5) from "coldplay/albums" routing "1",
  get(7) from "coldplay/albums" version 5
)
```
