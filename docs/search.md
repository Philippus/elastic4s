## Search



To execute a [search](http://www.elasticsearch.org/guide/reference/api/search/) in elastic4s, we need to pass an instance of `SearchRequest` to our client.

One way to do this is to invoke `search` and pass in the index name. From there, you can call
`query` and pass in the type of query you want to perform.

For example, to perform a simple text search, where the query is parsed from a single string we can do:

```scala
search("cities").query("London")
```


This delegates to the catchily named [Simple-Query-String-Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html).
The string is implicitly converted to an instance of type of `QueryStringQuery`.
In other words, it is the same as doing this:

```scala
search("cities").query(simpleStringQuery("London"))
```

We can search for everything by not specifying a query at all.
```scala
search("cities")
```

### Query Types

The different [query types](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html) available in elasticsearch are used in elastic4s by either creating instances of them directly, or using the DSL shortcut methods.



For example, to create a [Term-Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html):

```scala
search("cities").query(termQuery("country", "France"))
```

Or a [Prefix-Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html):

```scala
search("cities").query(prefixQuery("country", "France"))
```

Or by a [Regexp-Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html) (slow, but handy sometimes!):

```scala
search("cities").query(regexQuery("country", "France"))
```



We can combine queries using the [Bool-Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html) type.

```scala
search("cities").query(
   boolQuery().must(
     matchQuery("states.name", "Montana"),
     matchQuery("states.entry", 1889)
   )
)
```

And so on, for all the other query types such as range for numeric fields, wildcards, distance, geo shapes, matching.

It is also possible to use raw json queries.
This provides more flexibility (for when elastic4s is missing an option, or a new query type) and enables storing queries in a separate environment (DB, config file, etc.).

```scala
search("myindex").rawQuery(
  """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
)
```


### Search Settings

We might want to limit the number of results and / or set the starting offset.

```scala
search("cities").query("paris").start(5).limit(10)
```


### Sorting


Elastic4s provides [sorting](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-sort) of course.

```scala
search("cities") query "europe" sort (
    fieldSort("name"),
    fieldSort("status")
)
```

Other sort types, such as geoSort and [scriptSort](https://www.elastic.co/guide/en/elasticsearch/painless/7.6/painless-sort-context.html) are also provided.




### Aggregations

The [Aggregations](http://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html) framework helps provide aggregated data based on a search query.

The basic way of doing aggregations in elastic4s is by passing instances of `Aggregation` to the `aggs` method on the `SearchRequest`.
We can create these instances directly or by using the DSL. For example:

```scala
client.execute {
  search("sass_customers")
    .matchQuery("is_active", "true")
    .aggs {
      termsAgg("my_agg", "payment_status") // creates a terms aggregation on the payment_status field
    }
}
```

That would create an aggregation with the name `my_agg` of the most common terms in the `payment_status` field.


Aggregations can have sub aggregations which execute based on the buckets of the parent aggregation.

```scala
client.execute {
  search("prducts")
    .matchQuery("description", "cellphone")
    .aggs {
      termsAgg("my_agg", "color") // creates a terms aggregation on the color field
          .subaggs(
             avgAgg("avg_agg", "price") // creates an average aggregation on the price field
           )
    }
}
```


### Source Filtering

We can control which parts of the source are returned to us using source filtering.
We can specify which ones are included / excludes by using the `sourceInclude` and `sourceExclude` functions.
This is useful functionality to trim down large documents from being sent over the wire.

In this example, we want to return fields such as as population and gps, and exclude denonym and anything beginning with `capit`

```scala
client.execute {
  search("cities").query("europe").sourceInclude("gps", "population").sourceExclude("denonymn", "capit*")
}
```

We can specify multiple includes/excludes and they recognize regular expressions. Read more in the [elasticsearch
docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-source-filtering.html)




### Script Fields

We can use script fields to evaluate a field for each hit.
Script fields can even operate on fields that are not stored.
Script fields can include parameters which can be accessed when the script is evaluted.

We can specify the script fields in a search query through the use of the `scriptfields` method.

```scala
search("tubestops")
  .query("wimbledon")
  .scriptfields(
    scriptField(
      "fare_cost",
      script("doc['zone'] * fare_per_zone").param("fare_per_zone" -> 3.00)
    )
  )
```


See the [script fields](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-script-fields.html) section of the elasticsearch guide for greater detail of the use of script fields in searches.






### Suggestions

The suggestions feature returns similar looking terms (suggestions) for some given text using a suggestor. Suggestions
can be specified as part of a search query.

We can ask elasticsearch to include suggestions by adding a `suggestions` block to the search query. Inside the suggestions block
we add suggestion entries, of type `term`, `fuzzyCompletion`, `completion`, or `phrase`.

You can have multiple suggestions (for different fields say, or different types) in a search query, so each suggestion
 needs a name, which is used to refer to it later. Elastic4s also allows you to look up the suggestion response from the original
 suggestion class, which is the preferred way as it also avoids some casting you would otherwise need to do.

#### Term Suggestion

The term suggestor looks for terms in the index that are closest to the input.
Let's start by creating an index with some data (note this example uses the Indexable typeclass).

```scala
client.execute(
  bulk(
    indexInto("my_index").source(Song("style", "taylor swift")),
    indexInto("my_index").source(Song("shake it off", "Taylor Swift")),
    indexInto("my_index").source(Song("a new england", "kirsty maccoll")),
    indexInto("my_index").source(Song("blank page", "taylor swift")),
    indexInto("my_index").source(Song("I want it all", "Queen")),
    indexInto("my_index").source(Song("I to break free", "Queen")),
    indexInto("my_index").source(Song("radio gaga", "Queen")),
    indexInto("my_index").source(Song("we are the champions", "Quoon"))
  )
).await // as always the await is just used to block in demo code
```

Now we can add a suggestion block that will work with the data above.

```scala
val mysugg = termSuggestion("mysugg").field("artist").text("taylor swaft")

val resp = client.execute {
  search("my_index").suggestions(mysugg)
}.await

// use the suggestion def created earlier to retrieve the suggestion response
resp.suggestion(mysugg).entry("taylor").options // is empty
resp.suggestion(mysugg).entry("swaft").options // contains ["swift"]
```

In the suggestion response are entries, where each entry refers to a term in the text supplied. So in that example,
the text was "taylor swaft" which will become two terms, "taylor" and "swaft" so the suggestion response also has
two entries. The options array is the actual suggested terms for the entry.

##### Force Suggestions

You will notice that the first time, "taylor", has no options. This is because it actually had a match, so by default
elasticsearch skips it. You can force suggestions even when the term matches by changing the mode:

```scala
val mysugg = termSuggestion("mysugg").field("artist").text("taylor swaft").mode(SuggestMode.Always)
```

`SuggestMode.Always` means always include suggestions, and SuggestMode.Popular means include suggestions if there were
more popular results than the ones that matched.
