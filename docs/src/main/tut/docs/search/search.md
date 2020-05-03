


## Aggregations

[Aggregations](http://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html) are the new [facets](http://www.elastic.co/guide/en/elasticsearch/reference/current/search-facets.html). The basic way of doing aggregations in elastic4s is this:

```scala
client.execute {
  search("sass_customers")
    .matchQuery("is_active", "true")
    .aggs {
      termsAgg("my_agg_statuses", "payment_status")
        .subaggs {
          termsAgg("my_agg_customers", "tenant_id")
        }
    }
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
  search("cities") query "europe" sourceInclude("gps", "populat*") sourceExclude("denonymn", "capit*")
}
```

We can specify multiple includes/excludes and they recognize regular expressions. Read more in the [elasticsearch
docs](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-source-filtering.html)

Other options provided are highlighting, suggestions, filters, scrolling, index boosts and scripting. See [the query dsl](http://www.elasticsearch.org/guide/reference/api/search/) for more information.

## Inner Hits

Since version 1.5.0, Elasticsearch has supported [inner hits](http://www.elastic.co/guide/en/elasticsearch/reference/1.5/search-request-inner-hits.html).

To do top level inner hits in elastic4s we can do:

```scala
search("index") inner (
  inner hit "name" path "path",
  inner hit "name" `type` "type"
)
```

And to use inner hits on nested queries we can do:

```scala
search("index") query {
  nestedQuery("somepath") inner {
    inner hits "name" from 2 size 10
  }
}
```

## Script Fields

We can use script fields to evaluate a field for each hit. Script fields can even operate on fields that are not stored. Script fields can include parameters which can be accessed when the script is evaluted.

We can specify the script fields in a search query through the use of the `scriptfields` method:

```scala
search("tubestops") query "wimbledon" scriptfields (
  script field "fare_cost" script "doc['zone'] * fare_per_zone" params Map("fare_per_zone" -> 3.00)
)
```

The general form of scriptfield DSL expressions is:

```
script field <field_name> script <script> (lang <lang_name>){0,1} (params <Map[String,Any]>){0,1}
```

See the [script fields](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-script-fields.html) section of the elasticsearch guide for greater detail of the use of script fields in searches.
