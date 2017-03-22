---
layout: docs
title:  "Search API"
section: "docs"
---

# Searching

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
search in "places"->"cities" query { termQuery("state", "georgia") }
```

We wouldn't be able to do very much if we couldn't combine queries. So here we combine three queries, 2 "musts" that must match the documents and 1 "not" that must not match the documents. This is what ElasticSearch calls a [boolean query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html). You'll see in this example that I don't like to vacation anywhere that is too hot, and I want to only vacation somewhere that is awesome and that where the name ends with 'cester' like Gloucester or Leicester.
```scala
search in "places"->"cities" query {
   bool {
       must(
           regexQuery("name", ".*cester"),
           termQuery("status", "Awesome")
       ) not (
            termQuery("weather", "hot")
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

Or initialize the entire SearchRequestBuilder from a json string. This provides more flexibility (i.e when the DSL is not complete) and enables storing queries in a separate environment (DB, cache, etc.).
```scala
search in "*" types("users", "tweets") limit 5 extraSource {
  """{ "query": { "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } } }"""
} searchType SearchType.Scan
```

Elasticsearch provides [sorting](http://www.elasticsearch.org/guide/reference/api/search/facets/) of course. So does elastic4s. You can even include multiple sorts - rather like multiple order clauses in an SQL query.

```scala
search in "places"->"cities" query "europe" sort (
    field sort "name",
    field sort "status"
)
```

## Aggregations

[Aggregations](http://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html) are the new [facets](http://www.elastic.co/guide/en/elasticsearch/reference/current/search-facets.html). The basic way of doing aggregations in elastic4s is this:

```scala
client.execute {
  search in "index" / "type" query <somequery> aggregations(
    aggregation terms "agg1" field "field1" size 20,
    aggregation avg "agg2" field "field2"
  )
}
```

That would create two aggregations, one for the number of terms up to the 20 most common values in "field1", and one for the avg of the values in "field2"

#### Source Filtering

We can control which parts of the source are returned to us using source filtering. Let's carry on our places/cities
example, but now lets suppose the document has many more fields, such as population, foundation date,
gps coordinates. We can specify which ones are included / excludes by using the `sourceInclude` and `sourceExclude`
methods. This is useful functionality to trim down large documents from being sent over the wire.

```scala
client.execute {
  search in "places/cities" query "europe" sourceInclude("gps", "populat*") sourceExclude("denonymn", "capit*")
}
```

We can specify multiple includes/excludes and they recognize regular expressions. Read more in the [elasticsearch
docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-source-filtering.html)

Other options provided are highlighting, suggestions, filters, scrolling, index boosts and scripting. See [the query dsl](http://www.elasticsearch.org/guide/reference/api/search/) for more information.

## Inner Hits

Since version 1.5.0, Elasticsearch has supported [inner hits](http://www.elastic.co/guide/en/elasticsearch/reference/1.5/search-request-inner-hits.html).

To do top level inner hits in elastic4s we can do:

```scala
search in "index" / "type" inner (
  inner hit "name" path "path",
  inner hit "name" `type` "type"
)
```

And to use inner hits on nested queries we can do:

```scala
search in "index" / "type" query {
  nestedQuery("somepath") inner {
    inner hits "name" from 2 size 10
  }
}
```

## Script Fields

We can use script fields to evaluate a field for each hit. Script fields can even operate on fields that are not stored. Script fields can include parameters which can be accessed when the script is evaluted.

We can specify the script fields in a search query through the use of the `scriptfields` method:

```scala
search in "tubestops" query "wimbledon" scriptfields (
  script field "fare_cost" script "doc['zone'] * fare_per_zone" params Map("fare_per_zone" -> 3.00)
)
```

The general form of scriptfield DSL expressions is:

```
script field <field_name> script <script> (lang <lang_name>){0,1} (params <Map[String,Any]>){0,1}
```

See the [script fields](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-script-fields.html) section of the elasticsearch guide for greater detail of the use of script fields in searches.
