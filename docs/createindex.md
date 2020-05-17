## Creating Indexes

By default Elasticsearch does not require us to define indexes or their fields before we use them.
Indexes and types are created when they are first accessed, then updated with extra fields as more data is indexed.

Elasticsearch does a good job of guessing what we want but sometimes we need to override the defaults to better match our requirements (providing default values for fields or changing the way fields are analyzed).
This is achieved by providing Elasticsearch with a [mapping](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping.html) before we start indexing data.

Note that creating a mapping does not stop Elasticsearch from dynamically creating or updating types - anything that is not found in the mapping will still be dynamically updated.

### Building a Basic Mapping

Lets start by defining an index called `cities` which has a single field (`year_founded`):

```scala
import com.sksamuel.elastic4s.ElasticDsl._

client.execute {
  createIndex("cities").mapping(
    properties(
      IntegerField("year_founded")
    )
  )
}
```

Lets enhance this by adding a `location` field of type `GeoPointType`. This field type allows us to perform queries based on geographic locations
(e.g. find places within x miles of London):

```scala
client.execute {
  createIndex("cities").mapping(
    properties(
      IntegerField("year_founded"),
      GeoPointField("location")
    )
  )
}
```

Wrapping up our introduction, we finally want to add a field called [demonym](http://en.wikipedia.org/wiki/Demonym) to the city.
If there is no demonym (ie, the field is empty / null when indexing) we'll tell Elasticsearch to use a default of "citizen".
We definitely don't want any stemming on these words. If you're from Brussels, you are a Bruxellois. We don't want the `s` to be removed.
Therefore we'll also tell Elasticsearch to index the whole word as a single token by using the `KeywordAnalyzer`

```scala
client.execute {
  createIndex("cities").mapping(
    properties(
      IntegerField("year_founded"),
      GeopointField("location"),
      TextField("demonym").nullValue("citizen").analyzer(KeywordAnalyzer)
    )
  )
}
```

There are many options that can be specified a field or type. The [reference](#create_index_reference) section below describes how many of these features can be used. For a full list, see the methods in the DSL or see the official documentation on [mapping](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping.html) (the DSL keywords will be the same or very close to the name of the optional in the REST API).

### Create Geo Shape Index

In the first example we created a `type` of `GeoPointType`. Elasticsearch also offers a `GeoShapeType` type, which allows you to
store [different geojson formats](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html#_input_structure_2) like _LineString_, _Polygon_ or _Point_. Create a mapping for a `GeoShapeType` like this

```scala
client.execute {
  createIndex("areas").mapping(
    properties(
      geoshapeField("location")
    )
  )
}
```

Sometimes you want to be more precise about the settings for your `GeoShapeType`. Elasticsearch provides [different tree and precision settings](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html#_example_4) for geo shapes. In order to add these we extend the example:

```scala
client.execute {
  createIndex("areas").mapping(
    properties(
      geoshapeField("location") tree PrefixTree.Quadtree precision "1m"
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
  createIndex("countries").mapping(
    properties(
      textField("year_founded") analyzer SimpleAnalyzer
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

More information on custom analyzers can be found in the [Analyzers guide](../misc/analyzers.md).
