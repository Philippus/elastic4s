---
layout: docs
title:  "Optimize API"
section: "docs"
---

# Optimize

The optimize API allows to optimize one or more indexes for faster search by merging the number of
Lucene segments (which grow over time). Read more on the
[official documentation](http://www.elasticsearch.org/guide/en/elasticsearch/reference/master/indices-optimize.html).

There isn't really a whole lot to using this DSL. Simply specify a single index or a sequence of indexes.

```scala
client.execute {
  optimize index "index1"
}
```

or

```scala
client.execute {
  optimize("index1", "index2", ....)
}
```

There are only a few options. The most useful is maxSegments which allows you to fully optimize by choosing
to optimize to a single (or whatever you want) segments, eg

```scala
client.execute {
  optimize index "index1" maxSegments 6
}
```

The rest of the options are described
[here](http://www.elasticsearch.org/guide/en/elasticsearch/reference/master/indices-optimize.html).
