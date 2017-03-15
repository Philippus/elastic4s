---
layout: docs
title:  "Create Index API"
section: "docs"
---

# Creating Indexes

By default Elasticsearch does not require us to define indexes or their fields before we use them. Indexes and types are created when they are first accessed, then updated with extra fields as more data is indexed.

Elasticsearch does a good job of guessing what we want but sometimes we need to override the defaults to better match our requirements (providing default values for fields or changing the way fields are analyzed). This is achieved by providing Elasticsearch with a [mapping](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping.html) before we start indexing data.

Note that creating a mapping does not stop Elasticsearch from dynamically creating or updating types - anything that is not found in the mapping will still be dynamically updated.

## Building a Basic Mapping

Lets start by defining an index called `places` that has a single type (`city`) which has a single field (`year_founded`):

```scala
// imports - will be omitted for other examples
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._

client.execute {
  create index "places" mappings (
    "city" as (
      "year_founded" typed IntegerType
    )
  )
}
```

Lets enhance this by adding a `location` field of type `GeoPointType`. This field type allows us to perform queries based on geographic locations
(e.g. find places within x miles of London):

```scala
client.execute {
  create index "places" mappings (
    "city" as (
      "year_founded" typed IntegerType,
      "location" typed GeoPointType
    )
  )
}
```

**Sidenote:** You might wonder why the DSL keyword is called `typed`. This is because `type` is a keyword in Scala and `"location" typed GeoPointType` looks better than ```"location" `type` GeoPointType```.

Now some places have names that include common stop words such as _"The Congo"_ or _"Bay of Biscay"_, if we let Elasticsearch use its defaults these stop words would be removed when searching. This is not what we want so we'll use a `SimpleAnalyzer` which only lowercases the text and splits on non-letters.

In this next example we'll add a new type, called country, and we'll change the analyzer as mentioned:

```scala
client.execute {
  create index "places" mappings (
    "city" as (
      "year_founded" typed IntegerType,
      "location" typed GeoPointType
    ), // note trailing comma as this is a var-args invocation
    "country" as (
      "name" typed StringType analyzer SimpleAnalyzer
    )
  )
}
```

Elasticsearch is a clever beast. One small example is that it can detect dates automatically in fields instead of us setting the `DateType` in mappings. This is useful when you don't know all the fields in advance. To set the formats that Elasticsearch will recognize, we can use `date_detection` at the mapping level. Going back to our places example we'll let Elasticsearch detect our dates from two different formats (you can set as many as you need):

```scala
client.execute {
  create index "places" mappings (
    "city" as (
      "year_founded" typed IntegerType,
      "location" typed GeoPointType
    ),
    "country" as (
      "name" typed StringType analyzer SimpleAnalyzer
    ) dateDetection true dynamicDateFormats("dd/MM/yyyy", "dd-MM-yyyy")
  )
}
```

Wrapping up our introduction, we finally want to add a field called [demonym](http://en.wikipedia.org/wiki/Demonym) to the city. If there is no demonym (ie, the field is empty / null when indexing) we'll tell Elasticsearch to use a default of "citizen". We definitely don't want any stemming on these words. If you're from Brussels, you are a Bruxellois. We don't want the `s` to be removed. Therefore we'll also tell Elasticsearch to index the whole word as a single token.

```scala
client.execute {
  create index "places" mappings (
    "city" as (
      "year_founded" typed IntegerType,
      "location" typed GeoPointType,
      "demonym" typed StringType nullValue "citizen" analyzer KeywordAnalyzer
    ),
    "country" as (
      "name" typed StringType analyzer SimpleAnalyzer
    ) dateDetection true dynamicDateFormats("dd/MM/yyyy", "dd-MM-yyyy")
  )
}
```

There are many options that can be specified a field or type. The [reference](#create_index_reference) section below describes how many of these features can be used. For a full list, see the methods in the DSL or see the official documentation on [mapping](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping.html) (the DSL keywords will be the same or very close to the name of the optional in the REST API).

## Create Geo Shape Index

In the first example we created a `type` of `GeoPointType`. Elasticsearch also offers a `GeoShapeType` type, which allows you to
store [different geojson formats](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html#_input_structure_2) like _LineString_, _Polygon_ or _Point_. Create a mapping for a `GeoShapeType` like this

```scala
client.execute {
  create index "areas" mappings (
    "parks" as (
      "location" typed GeoShapeType
    )
  )
}
```

Sometimes you want to be more precise about the settings for your `GeoShapeType`. Elasticsearch provides [different tree and precision settings](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html#_example_4) for geo shapes. In order to add these we extend the example:

```scala
client.execute {
  create index "areas" mappings (
    "parks" as (
      "location" typed GeoShapeType tree PrefixTree.Quadtree precision "1m"
    )
  )
}
```

## Create Index Reference

### Index Settings

Basic index settings including shard count, replica count and refresh interval can be specified during index creation:

```scala
client.execute {
  create index "places" shards 3 replicas 2 refreshInterval "10s" mappings (/* mappings... */)
}
```

Other index settings can be specified using `indexSetting`:

```scala
client.execute {
  create index "places" indexSetting("compound_on_flush", false) mappings (/* mappings... */)
}
```

For further documentation on index settings see the [official documentation](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-create-index.html#create-index-settings).

### Analyzers

Analyzers can be specified on a per-field basis:

```scala
client.execute {
  create index "places" mappings (
    "country" as (
      "name" typed StringType analyzer SimpleAnalyzer
    )
  )
}
```

The available built-in analyzers are:

* [WhitespaceAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-whitespace-analyzer.html)
* [StandardAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-standard-analyzer.html)
* [SimpleAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-simple-analyzer.html)
* [StopAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-stop-analyzer.html)
* [KeywordAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-keyword-analyzer.html)
* [PatternAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-pattern-analyzer.html)
* [SnowballAnalyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-snowball-analyzer.html)

In addition you can define your own analyzers (they are really just combinations of filters and tokenizers). These can be
used by referencing them by name:

```scala
client.execute {
  (create index "places" mappings (
    "country" as (
      "name" typed StringType analyzer SimpleAnalyzer,
      "country_code" typed StringType analyzer "country_code_analyzer"
    )
  ) analysis (
     PatternAnalyzerDefinition("country_code_analyzer", regex = ",")
  ))
}
```

More information on custom analyzers can be found in the [Analyzers guide](../misc/analyzers.md).
