---
layout: docs
title:  "Count API"
section: "docs"
---

# Counting

A count request executes a query and returns a count of the number of matching documents for that query.
It can be executed across one or more indices and across one or more types. The query can be omitted for a total
count across the indexes.

To count all documents in an index regardless of type, we can do this.

```scala
client.execute {
  count from "places"
}
```

Or then narrow it down by a type.

```scala
client.execute {
  count from "places"->"london"
}
```

We can do multiple indexes and multiple types at once.

```scala
client.execute {
  count from Seq("places", "movies") types ("london", "paris", "scifi")
}
```

We can include a query to narrow down the results.

```scala
client.execute {
  count from "places" -> "london" where "borough" -> "westminster"
}
```

Also `where` is a synonym for `query`, so you could do

```scala
client.execute {
  count from "places" -> "london" query "borough" -> "westminster"
}
```

You can include any part of the QueryDSL inside the where clause.
