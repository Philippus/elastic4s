elastic4s - Elasticsearch Scala Client
=========

[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)
[![Coverage Status](https://coveralls.io/repos/sksamuel/elastic4s/badge.png?branch=master)](https://coveralls.io/r/sksamuel/elastic4s?branch=master)

Elastic4s is mostly a wrapper around the standard Elasticsearch Java client with the intention of leveraging Scala to
create a concise, idiomatic, reactive, type safe DSL to write Elasticsearch requests. The Java client, which can of
course be used directly in Scala, is more verbose due to Java's verbose nature. Scala lets us do better.

Elastic4s's DSL allows you to to construct your requests with syntatic and semantic errors manifested at compile time,
and uses standard Scala futures to enable you to easily integrate into your existing asynchronous frameworks. The aim of
the DSL is that requests are written in an SQL-like way, while staying true to the Java API.

Elastic4s supports Scala collections so you don't have to do tedious conversions from your Scala domain classes into
Java collections. It also allows you to index documents directly without having to extract and set fields manually -
eg from a case class, a JSON document, or a Map (or a custom source). Due to its typesafe nature Elastic4s is also a
good way to learn the options/commands available for any operation, as your IDE can use the types to show you what methods are available.

#### Key points

* Typesafe concise DSL
* Reactive / Uses Scala futures
* Supports Scala collections
* Wraps Java library
* SQL-like requests

#### Release

The latest release is 1.3.2 which is compatible with Elasticsearch 1.3.x. There are releases for both Scala 2.10 and Scala 2.11.
For releases that are compatible with earlier versions of Elasticsearch,
[search maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22).
For more information read [Using Elastic4s in your project](#using-elastic4s-in-your-project).

|Elastic4s Release|Target Elasticsearch version|
|-------|---------------------|
|1.4.x|1.4.x|
|1.3.x|1.3.x|
|1.2.x.x|1.2.x|
|1.1.x.x|1.1.x|
|1.0.x.x|1.0.x|
|0.90.13.x|0.90.13|

#### Dependencies

Starting from version 1.2.1.3, if you want to use Jackson for JSON in ObjectSource, the following dependencies are required in your project:

* "com.fasterxml.jackson.core"     %  "jackson-core"         % "2.4.1"
* "com.fasterxml.jackson.core"     %  "jackson-databind"     % "2.4.1"
* "com.fasterxml.jackson.module"   %% "jackson-module-scala" % "2.4.1"

## Introduction

The basic format of the DSL is that requests (eg a search request, a delete request, an update request, etc) are created using the DSL,
and then they are passed to the `execute` method on the client instance, which will return a response. The requests are
written in a style that is similar to SQL. Eg, `search in "index/type" query "findme"`

All requests on the client are asynchronous and will return a standard Scala `Future[T]` where T is the response type
appropriate to your request. For example a search request will return a response of type `SearchResponse`.
The response objects are, for the most part, the exact same type the Java API returns.
This is because there is mostly no reason to wrap these.

All the DSL keywords are located in the `ElasticDsl` trait which needs to be imported or extended.
The standard client is a class called `ElasticClient`. To create a client use the methods on the `ElasticClient` companion object.

An example is worth 1000 characters so here is a quick example of how to create a local node with a client
 and index a one field document. Then we will search for that document using a simple text query.

```scala
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

object Test extends App {

  val client = ElasticClient.local

  // await is a helper method to make this operation synch instead of asynch
  // You would normally avoid doing this is a real program as it will block
  client.execute { index into "bands/artists" fields "name"->"coldplay" }.await

  val resp = client.execute { search in "bands/artists" query "coldplay" }.await
  println(resp)

}
```

For more in depth examples keep reading.

## Syntax

Here is a list of the common requests and the syntax used to create them. For more details on each request click
through to the readme page. For options that are not yet documented, refer to the Elasticsearch documentation as
the DSL closely mirrors the standard Java API / REST API.

| Operation                                 | Samuel Normal Form Syntax |
|-------------------------------------------|----------------|
| [Create Index](guide/createindex.md)      | `create index <name> mappings { mappings block> } [settings]`|
| [Index](guide/index.md)                   | `index into <index/type> fields { <fieldblock> } [settings]` |
| [Search](guide/search.md)                 | `search in <index/type> query ... filter ... sort ...` |
| [Get](guide/get.md)                       | `get id <id> from <index/type> [settings]` |
| Get Mapping                               | `mapping from <index>` |
| [Count](guide/count.md)                   | `count from <indexes> types <types> <queryblock>` |
| [Delete by id](guide/delete.md)           | `delete id <id> from <index/type> [settings]`
| [Delete by query](guide/delete.md)        | `delete from <index/type> query { <queryblock> } [settings]`
| [Delete index](guide/delete.md)           | `delete index <index> [settings]`
| [Explain](guide/explain.md)               | `explain id <id> in <index/type> query { <queryblock> }`
| More like this                            | `morelike id <id> in <index/type> { fields <fieldsblock> } [settings]` |
| [Multiget](guide/multiget.md)             | `multiget ( get id 1 from index, get id 2 from index, ... )` |
| [Multisearch](guide/multisearch.md)       | `execute ( search in <index/type> query, search in <index/type> query, ...)`|
| [Update](guide/update.md)                 | `update id <id> in <index/type> script <script> [settings]` |
| [Optimize](guide/optimize.md)             | `optimize index "indexname" [settings]` |
| Register Query                            | `<id> into <index> query { <queryblock> }` |
| Percolate Doc                             | `percolate in <index> { fields <fieldsblock> }` |
| [Validate](guide/validate.md)             | `validate in "index/type" query <queryblock>` |
| Index Status                              | `status <index>` |
| [Add Alias](guide/aliases.md)             | `aliases add "<alias>" on "<index>"` |
| [Remove Alias](guide/aliases.md)          | `aliases remove "<alias>" on "<index>"` |
| Put mapping                               | `put mapping </index/type> add { mappings block }` |
| [Create Repository](guide/snapshot.md)    | `repository create <repo> type <type> settings <settings>` |
| [Create Snapshot](guide/snapshot.md)      | `snapshot create <name> in <repo> ...` |
| [Delete Snapshot](guide/snapshot.md)      | `snapshot delete <name> in <repo> ...` |
| [Restore Snapshot](guide/snapshot.md)     | `snapshot restore <name> from <repo> ...` |
| Create Index Template      | `template create <name> pattern <pattern> mappings {...}` |
| Delete Index Template      | `template delete <name>` |

Please also note [some java interoperability notes](guide/javainterop.md).

## Client

A locally configured node and client can be created simply by invoking `local` on the `ElasticClient` object:

```scala
import com.sksamuel.elastic4s.ElasticClient
val client = ElasticClient.local
```

To specify settings for the local node you can pass in a settings object like this:
```scala
val settings = ImmutableSettings.settingsBuilder()
      .put("http.enabled", false)
      .put("path.home", "/var/elastic/")
val client = ElasticClient.local(settings.build)
```

To connect to a remote elastic cluster then you need to use the remote() call specifying the hostnames and ports:
```scala
// single node
val client = ElasticClient.remote("host1", 9300)
// or for multiple nodes
val client = ElasticClient.remote("host1" -> 9300, "host2" -> 9300)
```

If you need to pass settings to the client, then you need to invoke remote() with a settings object.
For example to specify the cluster name (if you changed the default then you must specify the cluster name).

```scala
import org.elasticsearch.common.settings.ImmutableSettings
val settings = ImmutableSettings.settingsBuilder().put("cluster.name", "myClusterName").build()
val client = ElasticClient.remote(settings, ("somehost", 9300))
```

If you already have a handle to a Node in the Java API then you can create a client from it easily:
```scala
val node = ... // node from the java API somewhere
val client = ElasticClient.fromNode(node)
```

## Create Index

All documents in Elasticsearch are stored in an index. We do not need to tell Elasticsearch in advance what an index
will look like (eg what fields it will contain) as Elasticsearch will adapt the index as more documents are added,
but we must create at least create the index.

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
import com.sksamuel.elastic4s.mapping.FieldType._
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
It is still fully dynamic and other fields will be created as needed with default options. Only the fields mentioned
will be "fixed".

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
  index into "places/cities" id "uk" fields (
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

Sometimes it is useful to seperate the knowledge of the type from the indexing logic. For this we can use the
`DocumentSource` or `DocumentMap` abstractions. A quick example.

```scala
case class Band(name: String, albums: Seq[String], label: String)
val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

client.execute {
  // the band object will be implicitly converted into a DocumentSource
  index into "music/bands" doc band
}
```

Here Elastic4s has implicitly converted your case class into a DocumentSource and all fields would be indexed against
the field name. You can control this at a more fine grained level if required. More details on the [document traits](guide/source.md) page.

Beautiful!

## Searching

Searching is naturally the most involved operation.
There are many ways to do [searching in elastic search](http://www.elasticsearch.org/guide/reference/api/search/) and that is reflected
in the higher complexity of the query DSL.

To do a simple text search, where the query is parsed from a single string
```scala
search in "places"->"cities" query "London"
```

That is actually an example of a SimpleStringQueryDefinition. The string is implicitly converted to that type of query.
It is the same as specifying the query type directly:

```scala
search in "places"->"cities" query simpleStringQuery("London")
```

The simple string example is the only time we don't need to specify the query type.
We can search for everything by not specifying a query at all.
```scala
search in "places"->"cities"
```

We might want to limit the number of results and / or set the offset.
```scala
search in "places"->"cities" query "paris" start 5 limit 10
```

We can search against certain fields only:
```scala
search in "places"->"cities" query termQuery("country", "France")
```

Or by a prefix:
```scala
search in "places"->"cities" query prefixQuery("country", "France")
```

Or by a regular expression (slow, but handy sometimes!):
```scala
search in "places"->"cities" query regexQuery("country", "France")
```

There are many other types, such as range for numeric fields, wildcards, distance, geo shapes, matching.

Read more about search syntax [here](guide/search.md)
Read about [multisearch here](guide/multisearch.md)

## Get

Sometimes we don't want to search and want to retrieve a document directly from the index by id.
In this example we are retrieving the document with id 'coldplay' from the bands/rock index and type.

```scala
client.execute {
 get id "coldplay" from "bands/rock"
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
client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases
where the Scala DSL is missing a construct, or where there is no need to provide a DSL.

## Using Elastic4s in your project

For SBT users simply add:

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s" % "1.3.2"
```

For Maven users simply add (replace 2.10 with 2.11 for Scala 2.11):

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s_2.10</artifactId>
    <version>1.3.2</version>
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

## Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s
* Adding to the documentation

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013-2014 Stephen Samuel

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
