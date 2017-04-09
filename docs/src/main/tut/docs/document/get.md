---
layout: docs
title:  "Get API"
section: "docs"
---

# Get

The get request allows us to retrieve a document from an index by id.

First, import the ElasticDSL

```tut:silent
import com.sksamuel.elastic4s.ElasticDsl._
```

The format is simple.

```tut:book
get(8) from "beer/lager"
```

Which would return the document with id 8 from the beer index with type lager.

Like the other requests we can use a tuple to specifiy the index/type eg.

```tut:book
get(8) from "beer" -> "lager"
```

You can specify a version, which means the GET will only succeed if the version matches.

```tut:book
get(8) from "beer" -> "lager" version 12
```

If the document exists with version 12 then this will return a result, otherwise it will return no results.

Other options are realtime, routing, preference, versionType, fetchSourceContext. For more details on what these do, consult the official elasticsearch documents [here](http://www.elasticsearch.org/guide/en/elasticsearch/reference/master/docs-get.html).
