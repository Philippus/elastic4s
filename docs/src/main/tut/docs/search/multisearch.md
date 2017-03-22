---
layout: docs
title:  "Multi Search API"
section: "docs"
---

# Multi Search

The multisearch request type allows us to execute multiple searches in the a single request.
The format is simple, pass a list of search requests into the client method.

Then to issue multiple search requests at once, we wrap the search results in a multi block, as such:

```scala
val resp = client.execute (
  multi( // use ( not { as we are passing in var args, not a code block
    search in "jtull" query "mylo", // note the trailing comma, we are invoking a var args method
    search in "jtull" query "viva"
  )
)
```

The resp value is of type Future[MultiSearchResponse], the standard Java API response for multi searches.

Note, the scala client has no distinction in the syntax between multisearch and standard single search.
You simply choose to either invoke with a single search and get back a Future[SearchResponse] or 
invoke with many searches and get back a Future[MultiSearchResponse].

This is the only multi* method that can be invoked directly. Others need a wrapping block, eg multiget.
This is because the varargs method is desugered into a Seq[T] which becomes Seq[_] due to erasure.
