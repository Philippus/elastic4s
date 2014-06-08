## Searching

Searching is naturally the most involved operation. There are many ways to do [searching in elastic search](http://www.elasticsearch.org/guide/reference/api/search/) and that is reflected
in the higher complexity of the search DSL.

To do a simple string query search, where the search query is parsed from a single string
```scala
search in "places"->"cities" query "London"
```

We can search for everything by not specifying a query at all.
```scala
search in "places"->"cities"
```

We might want to limit the number of results and / or set the offset.
```scala
search in "places"->"cities" query "paris" start 5 limit 10
```

One of the great features of Elasticsearch is the number of queries it provides. Here we can use the term query to limit the search to just the state of Georgia rather than the country of Georgia.
```scala
search in "places"->"cities" { term("state", "georgia") }
```

We wouldn't be able to do very much if we couldn't combine queries. So here we combine three queries, 2 "musts" that must match the documents and 1 "not" that must not match the documents. This is what ElasticSearch calls a [boolean query](http://www.elasticsearch.org/guide/reference/query-dsl/bool-query/). You'll see in this example that I don't like to vacation anywhere that is too hot, and I want to only vacation somewhere that is awesome and that where the name ends with 'cester' like Gloucester or Leicester.
```scala
search in "places"->"cities" query {
   bool {
       must(
           regex("name", ".*cester"),
           term("status", "Awesome")
       ) not (
            term("weather", "hot")
       )
   }
}
```

It is also possible to use raw json queries. This provides more flexibility (i.e when the DSL is not complete) and enables storing queries in a separate environment (DB, cache, etc.).
```scala
search in "*" types("users", "tweets") limit 5 rawQuery {
  """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
} searchType SearchType.Scan
```


We might want to return facets from our search. Naturally in London we'd want to search for historic landmarks and the age of those attractions and so we'd offer these as selectable facets to our lovely users.
```scala
search in "places"->"cities" query "london" facets (
    facet terms "landmark" field "type",
    facet range "age" field "year" range (1000->1200) to(1200) from(1400)
)
```

Other facet types include geo distance, query, filter, range, date, histogram. The full documentation is [here](http://www.elasticsearch.org/guide/reference/api/search/facets/).

Elasticsearch provides [sorting](http://www.elasticsearch.org/guide/reference/api/search/facets/) of course. So does elastic4s. You can even include multiple sorts - rather like multiple order clauses in an SQL query.

```scala
search in "places"->"cities" query "europe" sort (
    by field "name",
    by field "status"
)
```

#### Source Filtering

We can control which parts of the source are returned to us using source filtering. Let's carry on our places/cities 
example, but now lets suppose the document has many more fields, such as population, foundation date, 
gps coordinates. We can specify which ones are included / excludes by using the `sourceInclude` and `sourceExclude` 
methods. This is useful functionality to trim down large documents from being sent over the wire.

```scala
val resp1 = client.sync.execute {
  search in "places/cities" query "europe" sourceInclude("gps", "populat*") sourceExclude("denonymn", "capit*")
}
```

We can specify multiple includes/excludes and they recognize regular expressions. Read more in the [elasticsearch 
docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-source-filtering.html)

Other options provided are highlighting, suggestions, filters, scrolling, index boosts and scripting. See [the query dsl](http://www.elasticsearch.org/guide/reference/api/search/) for more information.
