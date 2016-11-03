elastic4s - Elasticsearch Scala Client
=========

[![Join the chat at https://gitter.im/sksamuel/elastic4s](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sksamuel/elastic4s?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png?branch=master)](https://travis-ci.org/sksamuel/elastic4s)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10*.svg?label=latest%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11*.svg?label=latest%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)

Elastic4s is mostly a wrapper around the standard Elasticsearch Java client with the intention of creating a concise, idiomatic, reactive, type safe DSL for applications in Scala that use Elasticsearch. The Java client, which can of course be used directly in Scala, is more verbose due to Java's nature. Scala lets us do better.

Elastic4s's DSL allows you to to construct your requests programatically, with syntatic and semantic errors manifested at compile time, and uses standard Scala futures to enable you to easily integrate into your existing asynchronous workflow. The aim of the DSL is that requests are written in an SQL-like way, while staying true to the Java API or Rest API.

Elastic4s supports Scala collections so you don't have to do tedious conversions from your Scala domain classes into Java collections. It also allows you to index documents directly without having to extract and set fields manually - eg from a case class, a JSON document, or a Map (or a custom source). Due to its type safe nature, it is easy to see what operations are available for any request type, because your IDE can use type information to show what methods are available.

#### Key points

* Type safe concise DSL
* Integrates with standard Scala futures
* Uses Scala collections library over Java collections
* Returns `Option` where the java methods would return null
* Uses typeclasses for marshalling and unmarshalling of classes into elasticsearch documents
* Leverages the built-in Java client
* Provides [reactive-streams](#elastic-reactive-streams) implementation

#### Release

The latest release is 2.4.0 which is compatible with Elasticsearch 2.4.x. There are releases for both Scala 2.10 and Scala 2.11. For releases that are compatible with earlier versions of Elasticsearch,
[search maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22).
For more information read [Using Elastic4s in your project](#using-elastic4s-in-your-project).

The upcoming 3.0.0 release will be compatible with Elasticsearch 5.0.0.

|Elastic4s Release|Target Elasticsearch version|
|-------|---------------------|
|3.0.x (In progress)|5.0.x|
|2.4.x|2.4.X|
|2.3.x|2.3.X|
|2.2.1|2.2.X|
|2.1.2|2.1.X|
|2.0.1|2.0.X|
|1.7.5|1.7.X|
|1.6.6|1.6.X|
|1.5.17|1.5.X|
|1.4.14|1.4.x|
|1.3.3|1.3.x|
|1.2.3.0|1.2.x|
|1.1.2.0|1.1.x|
|1.0.3.0|1.0.x|
|0.90.13.2|0.90.x|

##### Changelog


###### 2.1.1

* #484 Fixed bug in es-streams throwing ClassCastException
* #483 Added overloaded doc method to update to accept indexables

###### 2.1.0

* Optimize was renamed to ForceMerge. The existing optimize method are deprecated and `forceMerge(indexes*)` has been added in its place.
* #395 Added pipeline aggregation definitions
* #458 Added parameters to clear cache operation
* #475 Fixed breaking change in terms query
* `rewrite` was removed from Elasticsearch's matchXXX queries so has been removed in the dsl
* Added [GeoCentroid](https://www.elastic.co/guide/en/elasticsearch/reference/2.1/search-aggregations-metrics-geocentroid-aggregation.html) aggregation
* Added `terminateAfter` to search definition
* SearchType.SCAN is now deprecated in Elasticsearch
* `count` is deprecated in Elasticsearch and should be replaced with a search with size 0

###### 2.0.1

* #473 Added missing "filter" clause from bool query
* #475 Fixed breaking change in terms query
* #474 Added "item" likes to more like this query

###### 2.0.0

Major upgrade to Elasticsearch 2.0.0 including breaking changes. _Please raise a PR if I've missed any breaking changes._

* In elasticsearch 2.0.0 one of the major changes has been filters have become queries. So in elastic4s this means all methods `xxxFilter` are now `xxxQuery`, eg `hasChildrenFilter` is now `hasChildrenQuery`.
* Some options that existed only on filters like cache and cache key are now removed.
* Fuzzy like this query has been removed (this was removed in elasticsearch itself)
* Script dsl has changed. To create a script to pass into a method, you use `script(script)` or `script(name, script)` with further parameters set using the builder pattern.
* DynamicTemplate dsl `template name <name>` has been removed. Now you supply the full field definition in the dsl method, as such `template(field("price_*", DoubleType))`
* Index_analyzer has been removed in elasticsearch. Use analyzer and then override the analyzer for search with search_analyzer
* MoreLikeThis was removed from elasticsearch in favour of a `moreLikeThisQuery` on a search request.
* `moreLikeThisQuery` has changed camel case (capital L), also now requires the 'like' text as the 2nd method, eg `moreLikeThisQuery("field").text("a")` (both can take varargs as well).
* Search requests now return a richer response type. Previously it returned the java type. The richer type has java style methods so your code will continue to compile, but with deprecation warnings.
* The sorting DSL has changed in that the previous infix style methods are deprecated. So `field sort x` becomes `fieldSort(x)` etc.
* Or and And filters have been removed completely (not changed into queries like other filters). Use a bool query with `must` clauses for and's and `should` clauses for or's.
* Highlight dsl has changed slightly, `highlight field x` is now deprecated in favour of `highlight(x)`
* Delete mapping has been removed (this is removed in elasticsearch itself)
* IndexStatus api has been removed (this was removed in elasticsearch itself)
* Template has been renamed dynamic template (to better match the terminology in elasticsesarch)
* Field and mapping syntax has changed slightly. The implicit `"fieldname" as StringType ...` has been deprecated in favour of `field("fieldname", StringType)` or `stringField()`, `longField`, etc
* In es-streams the ResponseListener has changed to accept a `BulkItemResult` instead of a `BulkItemResponse`
* Multiget now returns a rich scala wrapper in the form of `MultiGetResult`. The richer type has java style methods so your code will continue to compile, but with deprecation warnings.
* GetSegments returns a scala wrapper in the form of `GetSegmentsResult`
* IndexStats returns a scala wrapper in the form `IndexStatsResult`

###### 1.7.0

* Works with Elasticsearch 1.7.x
* Removed sync client (deprecated since 1.3.0)

###### 1.6.6

* Fix for race condition in elastic-streams subscriber

###### 1.6.5
* Added sourceAsUpsert to allow `Indexable` as upsert in update queries
* Added geohash aggregation
* Added geohash cell filter
* Added cluster state api
* Added support for unmapped_type property
* Added block until index/type exists testkit helpers
* Added raw query to count dsl
* Added `show` typeclass for count
* Added `InFilter` and `IndicesFilter`
* Added shorter syntax for field types, eg `stringField(name)` vs `field name <name> typed StringType`

###### 1.6.4
* Added reactive streams implementation for elastic4s.
* Support explicit field types in the update dsl
* Added missing options to restore snapshot dsl
* Added `show` typeclass for percolate register

###### 1.5.17
* Added clear scroll api
* Added missing options to restore snapshot dsl

###### 1.6.3
* Added clear scroll api
* Added `show` typeclass for multisearch
* Allow update dsl to use explicit field values

###### 1.5.16
* Added `HitAs` as a replacement for the `Reader` typeclass
* Added indices option to mapping, count and search dsl
* Added docValuesFormat to timestamp mapping

###### 1.6.2
* Added new methods to testkit
* Introduced simplier syntax for sorts
* Added `HitAs` as a replacement for the `Reader` typeclass
* Fixed validate query for block queries
* Added `show` typeclasses for search, create index, into into, validate, count, and percolate to allow easy debugging of the json of requests.

###### 1.5.15
* Added `matched_fields` and highlight filter to highlighter

###### 1.6.1
* Added IterableSearch for iterating over a scroll
* Enhanced multiget dsl to include `routing`, `version` and `field` options
* Added rich result for GetAliasResponse
* Added context queries to suggestions
* Breaking change: Changed syntax of suggestions to be clearer and allow for type safe results
* Allow setting analyzer by name on matchphraseprefix
* Added singleMethodSyntax variant, eg `indexInto(index)` rather than `index into index`
* Added re-write to validate
* Added filter support to alias (previously only the java client filters were supported)
* Added cluster settings api
* Added field stats api
* Addd `docValuesFormat` to timestamp mapping
* Added `matched_fields` and highlight filter to highlighter
* Supported `stopwords_list` in filter
* Reworked testkit to allow more configuration over the creating of the test clients

#### Dependencies

Starting from version 1.5.13 the main artifact has been renamed to elastic4s-core_2.x. Please update your build scripts. There is now an elastic4s-testkit_2.x which brings in a couple of useful methods for waiting until the node/cluster is in some expected state. Very useful when trying unit tests.

If you previously used the Jackson support for DocumentSource or Indexables then you need to add a new dependency elastic4s-jackson_2.x. This will allow you to do `import ElasticJackson.Implicits._` which puts a Jackson based `Indexable` into scope, which allows any class to be indexed automagically without the need to manually create maps or json objects. Similarly, if you are using `response.hitsAs[T]`, then the same import brings in a `Reader` that will convert any type to a case class.

## Introduction

The basic usage of the Scala driver is that you create an instance of `ElasticClient` and then invoke the various `execute` methods with the requests you want to perform. The execute methods are asynchronous and will return a standard Scala `Future[T]` where T is the response type appropriate for your request type. For example a search request will return a response of type `SearchResponse` which contains the results of the search.

Requests, such as inserting a document, searching, creating an index, etc, are created using the DSL syntax that is similar in style to SQL queries. For example to create a search request, you would do: `search in "index/type" query "findthistext"`

The response objects are, for the most part, the exact same type the Java API returns.
This is because there is mostly no reason to wrap these as they are fairly easy to use in Scala.

All the DSL keywords are located in the `ElasticDsl` trait which needs to be imported or extended.

An example is worth 1000 characters so here is a quick example of how to create a local node with a client
 and index a one field document. Then we will search for that document using a simple text query.

```scala
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

object Test extends App {

  val client = ElasticClient.local

  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block your thread
  client.execute { index into "bands" / "artists" fields "name"->"coldplay" }.await

  // we need to wait until the index operation has been flushed by the server.
  // this is an important point - when the index future completes, that doesn't mean that the doc
  // is necessarily searchable. It simply means the server has processed your request and the doc is
  // queued to be flushed to the indexes. Elasticsearch is eventually consistent.
  // For this demo, we'll simply wait for 2 seconds (default refresh interval is 1 second).
  Thread.sleep(2000)

  // now we can search for the document we indexed earlier
  val resp = client.execute { search in "bands" / "artists" query "coldplay" }.await
  println(resp)

}
```

For more in depth examples keep reading.

## Syntax

Here is a list of the common requests and the syntax used to create them. For more details on each request click
through to the readme page. For options that are not yet documented, refer to the Elasticsearch documentation as
the DSL closely mirrors the standard Java API / REST API.

| Operation                                 | Syntax |
|-------------------------------------------|----------------|
| [Add Alias](guide/aliases.md)             | `add alias "<alias>" on "<index>"` |
| Cancel Tasks                              | `cancelTasks(nodeIds)` |
| Clear index cache                         | `clear cache <name>` |
| Close index                               | `closeIndex(<name>)` |
| [Count](guide/count.md)                   | `count from <indexes> types <types> <queryblock>` |
| Cluster health                            | `get cluster health` |
| Cluster stats                             | `get cluster stats` |
| [Create Index](guide/createindex.md)      | `createIndex(<name>) mappings { mappings block> } [settings]`|
| [Create Repository](guide/snapshot.md)    | `createRepository(<repo>) type(<type>) settings <settings>` |
| [Create Snapshot](guide/snapshot.md)      | `createSnapshot(<name>) in <repo> ...` |
| Create Template                           | `createTemplate(<name>) pattern <pattern> mappings {...} [settings]`|
| [Delete by id](guide/delete.md)           | `delete id <id> from <index/type> [settings]`
| [Delete index](guide/delete.md)           | `deleteIndex(<index>) [settings]`
| [Delete Snapshot](guide/snapshot.md)      | `deleteSnapshot(<name>).in(<repo>) ...` |
| Delete Template                           | `deleteTemplate(<name>)` |
| [Explain](guide/explain.md)               | `explain id <id> in <index/type> query { <queryblock> }`
| Field stats                               | `field stats <indexes>` |
| Flush Index                               | `flush index <name>` |
| [Get](guide/get.md)                       | `get id <id> from <index/type> [settings]` |
| Get Alias                                 | `getAlias(<name>).on(<index>)` |
| Get Mapping                               | `getMapping(<index> / <type>)` |
| Get Segments                              | `getSegments(<indexes>)` |
| Get Snapshot                              | `getSnapshot <name> from <repo>` |
| Get Template                              | `getTemplate(<name>)` |
| [Index](guide/index.md)                   | `index into <index/type> fields { <fieldblock> } [settings]` |
| Index exists                              | `indexExists(<name>)` |
| Index Status                              | `indexStatus(<index>)` |
| List Tasks                                | `listTasks(nodeIds)` |
| More like this                            | `morelike id <id> in <index/type> { fields <fieldsblock> } [settings]` |
| [Multiget](guide/multiget.md)             | `multiget ( get id 1 from index, get id 2 from index, ... )` |
| [Multisearch](guide/multisearch.md)       | `multi ( search ..., search ..., ...)`|
| Open index                                | `openIndex(<name>)` |
| [Force Merge](guide/optimize.md)          | `forceMerge(<indexes>*) [settings]` |
| Percolate Doc                             | `percolateIn(<index>) doc <fieldsblock>` |
| Put mapping                               | `putMapping(<index> / <type>) as { mappings block }` |
| Recover Index                             | `recoverIndex(<name>)` |
| Refresh index                             | `refreshIndex(<name>)` |
| Register Query                            | `register id <id> into <index> query { <queryblock> }` |
| [Remove Alias](guide/aliases.md)          | `removeAlias(<alias>).on(<index>)` |
| [Restore Snapshot](guide/snapshot.md)     | `restore snapshot <name> from <repo> ...` |
| [Search](guide/search.md)                 | `search in <index/type> query ... postFilter ... sort ...` |
| Search scroll                             | `searchScroll(<scrollId>)` |
| Type Exists                               | `typesExists(<types>) in <index>` |
| [Update](guide/update.md)                 | `update id <id> in <index/type> script <script> [settings]` |
| [Validate](guide/validate.md)             | `validateIn(<index/type>) query <queryblock>` |

Please also note [some java interoperability notes](guide/javainterop.md).

## Client

A locally configured node and client can be created simply by invoking `local` on the `ElasticClient` object:

```scala
import com.sksamuel.elastic4s.ElasticClient
val client = ElasticClient.local
```

To specify settings for the local node you can pass in a settings object like this:
```scala
val settings = Settings.settingsBuilder()
      .put("http.enabled", false)
      .put("path.home", "/var/elastic/")
val client = ElasticClient.local(settings.build)
```

To connect to a remote elastic cluster then you need to use the remote() call specifying the hostnames and ports. Please note that this is the port for the TCP interface (normally 9300) and NOT the port you connect with when using HTTP (normally 9200).

```scala
// single node
val client = ElasticClient.remote("host1", 9300)
```

For multiple nodes it's better to use the elasticsearch client uri connection string. This is in the format `"elasticsearch://host:port,host:port,..."` (Note, no parameters can be added). For example:
```scala
val uri = ElasticsearchClientUri("elasticsearch://foo:1234,boo:9876")
val client = ElasticClient.remote(uri)
```

If you need to pass settings to the client, then you need to invoke remote() with a settings object.
For example to specify the cluster name (if you changed the default then you must specify the cluster name).

```scala
import org.elasticsearch.common.settings.Settings
val settings = Settings.settingsBuilder().put("cluster.name", "myClusterName").build()
val client = ElasticClient.remote(settings, ElasticsearchClientUri("elasticsearch://somehost:9300"))
```

If you already have a handle to a Node in the Java API then you can create a client from it easily:
```scala
val node = ... // node from the java API somewhere
val client = ElasticClient.fromNode(node)
```

## Create Index

All documents in Elasticsearch are stored in an index. We do not need to tell Elasticsearch in advance what an index
will look like (eg what fields it will contain) as Elasticsearch will adapt the index dynamically as more documents are added, but we must at least create the index first.

To create an index called "places" that is fully dynamic we can simply use:

```scala
client.execute { create index "places" }
```

We can optionally set the number of shards and / or replicas

```scala
client.execute { create index "places" shards 3 replicas 2 }
```

Sometimes we want to specify the properties of the fields in the index in advance.
This allows us to manually set the type of the field (where Elasticsearch might infer something else) or set the analyzer used,
or multiple other options

To do this we add mappings:

```scala
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.StopAnalyzer

client.execute {
    create index "places" mappings (
        "cities" as (
            "id" typed IntegerType,
            "name" typed StringType boost 4,
            "content" typed StringType analyzer StopAnalyzer
        )
    )
}
```

Then Elasticsearch is configured with those mappings for those fields only.
It is still fully dynamic and other fields will be created as needed with default options. Only the fields specified will be "fixed".

More examples on the create index syntax can be [found here](guide/createindex.md).

## Analyzers

Analyzers control how Elasticsearch parses the fields for indexing. For example, you might decide that you want
whitespace to be important, so that "band of brothers" is indexed as a single "word" rather than the default which is
to split on whitespace. There are many advanced options available in analayzers. Elasticsearch also allows us to create
custom analyzers. For more details [read about the DSL support for analyzers](guide/analyzers.md).

## Indexing

To index a document we need to specify the index and type and optionally we can set an id.
If we don't include an id then elasticsearch will generate one for us.
We must also include at least one field. Fields are specified as standard tuples.

```scala
client.execute {
  index into "places" / "cities" id "uk" fields (
    "name" -> "London",
    "country" -> "United Kingdom",
    "continent" -> "Europe",
    "status" -> "Awesome"
  )
}
```

There are many additional options we can set such as routing, version, parent, timestamp and op type.
See [official documentation](http://www.elasticsearch.org/guide/reference/api/index_/) for additional options, all of
which exist in the DSL as keywords that reflect their name in the official API.

## Indexing from Classes

Sometimes it is useful to index directly from your domain model, and not have to create maps of fields inline. For this
elastic4s provides the `Indexable` typeclass. Simply provide an implicit instance of `Indexable[T]` in scope for any
class T that you wish to index, and then you can use `source t` on the index request. For example:

```scala
// a simple example of a domain model
case class Character(name: String, location: String)

// how you turn the type into json is up to you
implicit object CharacterIndexable extends Indexable[Character] {
  override def json(t: Character): String = s""" { "name" : "${t.name}", "location" : "${t.location}" } """
}

// now the index request reads much cleaner
val jonsnow = Character("jon snow", "the wall")
client.execute {
  index into "gameofthrones" / "characters" source jonsnow
}
```

Some people prefer to write typeclasses manually for the types they need to support. Other people like to just have
it done automagically. For those people, elastic4s provides a [Jackson](http://wiki.fasterxml.com)
based implementation of `Indexable[Any]` that will convert anything to Json.
To use this, you need to add the [jackson extension](http://search.maven.org/#search|ga|1|elastic4s-jackson) to the
build.

The next step is to import the implicit into scope with `import ElasticJackson.Implicits._` where ever you
want to use the `source` method. With that implicit in scope, you can now pass any type you like to `source`
and Jackson will marshall it to json for you.

Another way that existed prior to the `Indexable` typeclass was the `DocumentSource` or `DocumentMap` abstractions.
For these, you provide an instance of `DocumentSource` that returns a Json String, or an instance of DocumentMap
that provides a `Map[String, Any]`.

```scala

case class Character(name: String, location: String)

case class CharacterSource(c: Character) extends DocumentSource {
  def json : String = s""" { "name" : "${c.name}", "location" : "${c.location}" } """
}

val jonsnow = Character("jon snow", "the wall")
client.execute {
  index into "music" / "bands" doc CharacterSource(jonsnow)
}
```

There isn't much difference, but the typeclass approach (the former) is considered more idomatic scala.
More details on the [document traits](guide/source.md) page.

Beautiful!

## Searching

Searching is naturally the most involved operation.
There are many ways to do [searching in elastic search](http://www.elasticsearch.org/guide/reference/api/search/) and that is reflected
in the higher complexity of the query DSL.

To do a simple text search, where the query is parsed from a single string
```scala
search in "places" / "cities" query "London"
```

That is actually an example of a SimpleStringQueryDefinition. The string is implicitly converted to that type of query.
It is the same as specifying the query type directly:

```scala
search in "places" / "cities" query simpleStringQuery("London")
```

The simple string example is the only time we don't need to specify the query type.
We can search for everything by not specifying a query at all.
```scala
search in "places" / "cities"
```

We might want to limit the number of results and / or set the offset.
```scala
search in "places" / "cities" query "paris" start 5 limit 10
```

We can search against certain fields only:
```scala
search in "places" / "cities" query termQuery("country", "France")
```

Or by a prefix:
```scala
search in "places" / "cities" query prefixQuery("country", "France")
```

Or by a regular expression (slow, but handy sometimes!):
```scala
search in "places" / "cities" query regexQuery("country", "France")
```

There are many other types, such as range for numeric fields, wildcards, distance, geo shapes, matching.

Read more about search syntax [here](guide/search.md).
Read about [multisearch here](guide/multisearch.md).
Read about [suggestions here](guide/suggestions.md).

## Search Conversion

By default Elasticsearch search responses contain an array of `SearchHit` instances which contain things like the id,
index, type, version, etc as well as the document source as a string or map. Elastic4s provides a means to convert these
back to meaningful domain types quite easily using the `HitAs[T]` typeclass. Provide an implementation of this typeclass, as
an in scope implicit, for whatever type you wish to marshall search responses into, and then you can call `as[T]` on the response.

A full example:

```scala
case class Character(name: String, location: String)

implicit object CharacterHitAs extends HitAs[Character] {
  override def as(hit: RichSearchHit): Character = {
    Character(hit.sourceAsMap("name").toString, hit.sourceAsMap("location").toString)
  }
}

val resp = client.execute {
  search in "gameofthrones" / "characters" query "kings landing"
}.await // don't block in real code

// .as[Character] will look for an implicit HitAs[Character] in scope
// and then convert all the hits into Characters for us.
val characters :Seq[Character] = resp.as[Character]

```

This is basically the inverse of the `Indexable` typeclass. And just like Indexable, there is a general purpose
Jackson `HitAs[Any]` implementation for those who wish to have some sugar.
To use this, you need to add the [jackson extension](http://search.maven.org/#search|ga|1|elastic4s-jackson) to the build.

The next step is to import the implicit into scope with `import ElasticJackson.Implicits._` where ever you
want to use the `as[T]` methods.

As a bonus feature of the Jackson implementation, if your domain object has fields called `_timestamp`, `_id`, `_type`, `_index`, or
`_version` then those special fields will be automatically populated as well.

## Highlighting

Elasticsearch can annotate results to show which part of the results matched the queries by using highlighting.
Just think when you're in google and you see the snippets underneath your results - that's what highlighting does.

We can use this very easily, just add a highlighting definition to your search request, where you set the field or fields to be highlighted. Viz:

```scala
search in "music" / "bios" query "kate bush" highlighting (
  highlight field "body" fragmentSize 20
)
```

All very straightforward. There are many options you can use to tweak the results. In the example above I have
simply set the snippets to be taken from the field called "body" and to have max length 20. You can set the number of fragments to return, seperate queries to generate them and other things. See the elasticsearch page on [highlighting](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-highlighting.html) for more info.

## Get

Sometimes we don't want to search and want to retrieve a document directly from the index by id.
In this example we are retrieving the document with id 'coldplay' from the bands/rock index and type.

```scala
client.execute {
 get id "coldplay" from "bands" / "rock"
}
```

We can get multiple documents at once too. Notice the following multiget wrapping block.

```scala
client.execute {
  multiget(
    get id "coldplay" from "bands/rock",
    get id "keane" from "bands/rock"
  )
}
```

See more [get examples](guide/get.md) and usage of multiget [here](guide/multiget.md)

## Deleting

In the rare case that we become tired of a band we might want to remove them. Naturally we wouldn't want to remove Chris Martin and boys so we're going to remove U2 instead.
We think they're a little past their best (controversial). This operation assumes the id of the document is "u2".

```scala
client.execute {
  delete id "u2" from "bands/rock"
}
```

We can take this a step further by deleting by a query rather than id.
In this sense the delete is very similar to an SQL delete statement.
In this example we're deleting all bands where their type is rap.

```scala
client.execute {
    delete from index "bands" types "rock" where termQuery("type", "rap")
}
```

See more about delete on the [delete page](guide/delete.md)

## Updates

We can update existing documents without having to do a full index, by updating a partial set of fields.

```scala
client.execute {
  update 25 in "scifi/starwars" docAsUpsert (
    "character" -> "chewie",
    "race" -> "wookie"
  )
}
```

Read more about updates and see [more examples](guide/update.md).

## More like this

If you want to return documents that are "similar" to   a current document we can do that very easily with the more like this query.

```scala
client.execute {
  morelike id 4 from "beers/lager" percentTermsToMatch 0.5
}
```

For all the options see [here](http://www.elasticsearch.org/guide/reference/query-dsl/mlt-query/).

## Bulk Operations

Elasticsearch is fast. Roundtrips are not. Sometimes we want to wrestle every last inch of performance and a useful way
to do this is to batch up requests. Elastic has guessed our wishes and created the bulk API. To do this we simply
wrap index, delete and update requests using the `bulk` keyword and pass to the `execute` method in the client.

```scala
client.execute {
  bulk (
    index into "bands/rock" fields "name"->"coldplay",
    index into "bands/rock" fields "name"->"kings of leon",
    index into "bands/pop" fields (
      "name"->"elton john",
      "best_album"->"tumbleweed connection"
    )
  )
}
```
A single HTTP or TCP request is now needed for 4 operations. In addition Elasticsearch can now optimize the requests,
by combinging inserts or using aggressive caching.

The example above uses simple documents just for clarity of reading; the usual optional settings can still be used.
See more information on the [bulk page](guide/bulk.md).

## Json Output

It can be useful to see the json output of requests in case you wish to tinker with the request in a REST client or your browser.
It can be much easier to tweak a complicated query when you have the instant feedback of the HTTP interface.

Elastic4s makes it easy to get this json where possible. Simply call `.show` on a request to get back a json string. Eg:

```scala
val req = search in "music" / "bands" query "coldplay" ...
println(req.show) // would output json
client.execute { req } // now executes that request
```

Not all requests have a json body. For example _get-by-id_ is modelled purely by http query parameters, there is no json body to output.
And some requests don't convert to json in the Java client so aren't yet supported by the `show` typeclass.
Also, for clarity, it should be pointed out that the client doesn't send JSON to the server, it uses a binary protocol. So the provided json
format should be treated as a debugging tool only.

The requests that support `.show` are `search`, `multi`, `create index`, `index into`, `validate`, `percolate`, `count`.

## Synchronous Operations

All operations are normally asynchronous. Sometimes though you might want to block - for example when doing snapshots
or when creating the initial index. You can call `.await` on any operation to block until the result is ready.
This is especially useful when testing.

```scala
val resp = client.execute { index into "bands/rock" fields ("name"->"coldplay", "debut"->"parachutes") }.await
resp.isInstanceOf[IndexResponse] // true
```

## Helpers

Helpers provide higher level APIs to work with Elasticsearch.

#### Reindexing data

Use the `reindex` helper to reindex data from source index to target index.

```scala
client.reindex(
  sourceIndex = "sourceIndex",
  targetIndex = "targetIndex",
  chunkSize = 500,
  scroll = "5m")
```

## DSL Completeness

As it stands the Scala DSL covers all of the common operations - index, create, delete, delete by query,
search, validate, percolate, update, explain, get, and bulk operations.
There is good support for the various settings for each of these -
more so than the Java client provides in the sense that more settings are provided in a type safe manner.

However there are settings and operations (mostly admin / cluster related) that the DSL does not yet
cover (pull requests welcome!).
In these cases it is necessary to drop back to the Java API.
This can be done by calling .java on the client object to get the underlying java elastic client,
or .admin to get the admin based client, eg, the following request is a Java API request.

```scala
client.admin.cluster.prepareHealth.setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases
where the Scala DSL is missing a construct, or where there is no need to provide a DSL.

## Elastic Reactive Streams

Elastic4s has an implementation of the [reactive streams](http://www.reactive-streams.org) api for both publishing and subscribing that is built
using Akka. To use this, you need to add a dependency on the elastic4s-streams module.

There are two things you can do with the reactive streams implementation. You can create an elastic subscriber, and have that
stream data from some publisher into elasticsearch. Or you can create an elastic publisher and have documents streamed out to subscribers.

### Integrate

First you have to add an additional dependeny to your `build.sbt`

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-streams" % "1.7.4"
```

Import the new API with

```scala
import com.sksamuel.elastic4s.streams.ReactiveElastic._
```

### Publisher

An elastic publisher can be created for any arbitrary query you wish, and then using the efficient search scroll API, the entire dataset that matches your query is streamed out to subscribers.

And make sure you have an Akka Actor System in implicit scope

`implicit val system = ActorSystem()`

Then create a publisher from the client using any query you want. You must specify the scroll parameter, as the publisher
uses the scroll API.

`val publisher = client.publisher(search in "myindex" query "sometext" scroll "1m")`

Now you can add subscribers to this publisher. They can of course be any type that adheres to the reactive-streams api,
so you could stream out to a mongo database, or a filesystem, or whatever custom type you want.

`publisher.subscribe(someSubscriber)`

If you just want to stream out an entire index then you can use the overloaded form:

`val publisher = client.publisher("index1", keepAlive = "1m")`

### Subscription

An elastic subcriber can be created that will stream a request to elasticsearch for each item produced by a publisher.
The subscriber can create index, update, or delete requests, so is a good way to synchronize datasets.

`import ReactiveElastic._`

And make sure you have an Akka Actor System in implicit scope.

`implicit val system = ActorSystem()`

Then create a subscriber, specifying the following parameters:

* A type parameter that is the type of object that the publisher will provide
* How many documents should be included per index batch (10-100 is usually good)
* How many concurrent batches should be in flight (usually around the number of cores)
* An optional `ResponseListener` that will be notified for each item that was successfully acknowledged by the es cluster
* An optional function that will be called once the subscriber has received all data. Defaults to a no-op
* An optional function to call if the subscriber encouters an error. Defaults to a no-op.

In addition there should be a further implicit in scope of type `RequestBuilder[T]` that will accept objects of T (the type produced by your publisher) and build an index, update, or delete request suitable for dispatchin to elasticsearch.

```scala
implicit val builder = new RequestBuilder[SomeType] {
  import ElasticDsl._
  // the request returned doesn't have to be an index - it can be anything supported by the bulk api
  def request(t: T): BulkCompatibleDefinition =  index into "index" / "type" fields ....
}
```
Then the subscriber can be created, and attached to a publisher:

```scala
val subscriber = client.subscriber[SomeType](batchSize, concurrentBatches, () => println "all done")
publisher.subscribe(subscriber)
```

## Using Elastic4s in your project

For gradle users, add:

```groovy
compile 'com.sksamuel.elastic4s:elastic4s-core_2.11:2.3.0'
```

For SBT users simply add:

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.3.0"
```

For Maven users simply add (replace 2.11 with 2.10 for Scala 2.10):

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s-core_2.11</artifactId>
    <version>2.3.0</version>
</dependency>
```

The above is just an example and is not always up to date. Check the latest released version on
[maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22)

## Building and Testing

This project is built with SBT. So to build
```
sbt compile
```

And to test
```
sbt test
```

Integration tests run on a local elastic that is created and torn down as part of the tests inside your standard temp
folder. There is no need to configure anything externally.

## Used By
* Barclays Bank
* HSBC
* Shazaam
* Graphflow
* Hotel Urbano
* Immobilien Scout
* Deutsche Bank
* HMRC
* Canal+

_Raise a PR to add your company here_

![youkit logo](https://www.yourkit.com/images/yklogo.png) YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.

## Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s
* Adding to the documentation

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013-2016 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
