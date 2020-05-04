## Multi Search

The multisearch request type allows us to execute multiple searches in the a single request.
The format is simple, pass a list of search requests into the client method.

Then to issue multiple search requests at once, we wrap the search results in a multi block, as such:

```scala
client.execute (
  multi(
    search("jtull").query("mylo"),
    search("jtull").query("viva")
  )
)
```

The resp value is of type `MultiSearchResponse`.

Note, the scala client has no distinction in the syntax between multisearch and standard single search.
You simply choose to either invoke with a single search and get back a `SearchResponse` or
invoke with many searches and get back a `MultiSearchResponse`.

