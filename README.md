elastic4s - Elasticsearch Scala Client
=========

Elastic4s is a concise, idiomatic, asynchronous, type safe Scala Client for Elasticsearch.
It provides a Scala DSL to construct your queries and (hopefully!) reducing errors and uses standard Scala futures to enable you to easily integrate into your existing asynchronous workflows.
Due to its typesafe nature elastic4s is also a good way to learn the options available for any operation,
as your IDE can use the type information to show you what methods are available.
Elastic4s also allows you to index JSON documents from standard
JSON libraries such as Jackson without having to unmarshall.

#### Key points

* Typesafe DSL
* Uses Scala futures
* Supports Scala collections
* Wraps Java library

#### Release

The latest release is 1.1.1.0 which is compatible with Elasticsearch 1.1.1. There are releases for both Scala 2.10 and Scala 2.11.
For releases that are compatible with earlier versions of Elasticsearch,
[search maven central](http://search.maven.org/#search%7Cga%7C1%7Celastic4s). 
The general format is that release a.b.c.d is compatible with Elasticsearch a.b.c. 
For more information read [Using Elastic4s in your project](#using-elastic4s-in-your-project).

|Elastic4s Release|Target Elasticsearch version|
|-------|---------------------|
|1.1.1.2|1.1.1|
|1.0.3.0|1.0.3|
|0.90.13.1|0.90.13|


[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)
[![Coverage Status](https://coveralls.io/repos/sksamuel/elastic4s/badge.png?branch=master)](https://coveralls.io/r/sksamuel/elastic4s?branch=master)

## Introduction

The basic format of the DSL is to create requests (eg a search request or delete request)
and pass them in to the execute methods on the client, which returns a response object.
All requests on the standard client are asynchronous and will return a standard Scala 2.10 Future[T]
where T is the response type appropriate to your request - eg a SearchResponse for a SearchRequest.
The response objects are the same type as in the Java API.

All the DSL constructs exist in the ElasticDsl object which needs to be imported.
The standard client is a class called ElasticClient.
To create a client use the methods on the ElasticClient companion object.

An example is worth 1000 characters so here is a quick example of how to create a local node with a client
 and index a one field document:

```scala
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

object Test extends App {

  val client = ElasticClient.local
  client execute { index into "bands/singers" fields "name"->"chris martin" }

}
```

In general the format of the DSL is to call the execute method on the client and
pass in a block that returns a request of the type you wish to execute.

For more in depth examples keep reading.

## Syntax

Here is a list of the common operations and the syntax used to create requests.

For more details on each operation click through to the readme page. For options that are not yet documented, refer
to the Elasticsearch documentation as the DSL closely mirrors the standard Java API.

| Operation                                 | Samuel Normal Form Syntax |
|-------------------------------------------|----------------|
| [Create Index](guide/createindex.md)      | `create index <name> mappings { mappings block> } [settings]`|
| [Index](guide/index.md)                   | `index into <index/type> fields { <fieldblock> } [settings]` |
| [Search](guide/search.md)                 | `search in <index/type> query ... filter ... sort ...` |
| [Get](guide/get.md)                       | `get id <id> from <index/type> [settings]` |
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
| Index Status                              | `status(<index>)` |
| [Add Alias](guide/aliases.md)             | `aliases add "<alias>" on "<index>"` |
| [Remove Alias](guide/aliases.md)          | `aliases remove "<alias>" on "<index>"` |

Please also note [some java interoperability notes](guide/javainterop.md).

## Client

A locally configured node and client can be created simply by invoking ```local``` on the client companion object:

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
val settings = ImmutableSettings.settingsBuilder().put("cluster.name", "myClusterName").build()
val client = ElasticClient.remote(settings, ("somehost", 9300))
```

If you already have a handle to a Node in the Java API then you can create a client from it easily:
```scala
val node = ... // node from the java API somewhere
val client = ElasticClient.fromNode(node)
```


## Create Index

 To create an index that is fully dynamic we can simply use

```scala
client.execute { create index "places" }
```

This will create an index called places. We can optionally set the number of shards and / or replicas

```scala
client.execute { create index "places" shards 3 replicas 2 }
```

Sometimes we want to specify the properties of the types in the index. This allows us to override a fields type, the analyzer used, whether we should store that field, etc. To do this we add mappings

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

Then Elasticsearch is configured with those mappings for those fields only. It is still fully dynamic and other fields will be created as needed with default options.

More examples on the create index syntax can be [found here](guide/createindex.md).

## Analyzers

Elasticsearch allows us to register (create) custom analyzers. For more details [read here](guide/analyzers.md).

## Indexing

To index a document we need to specify the index and type and optionally the id. We must also include at least one field.

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
See [official documentation](http://www.elasticsearch.org/guide/reference/api/index_/) for additional options.

Sometimes it is useful to seperate the knowledge of the type from the indexing logic. For this we can use the
DocumentSource or DocumentMap abstraction. A quick example.

```scala
val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

client.execute {
  index into "music/bands" doc band
}
```

More details on the [document traits](guide/source.md) page.

Beautiful!

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

Elasticsearch is fast. Roundtrips are not.
Sometimes we want to wrestle every last inch of performance and a useful way to do this is to batch up operations.
Elastic has predicted our wishes and created the bulk API.
To do this we simply combine index, delete and update operations into a sequence and execute using the bulk method in the client.

```scala
client.bulk {
   index into "bands/rock" fields "name"->"coldplay",
   index into "bands/rock" fields "name"->"kings of leon",
   index into "bands/pop" fields (
      "name"->"elton john",
      "best_album"->"goodbye yellow brick road"
   )
}
```
A single HTTP or TCP request is now needed for 4 operations.
The example above uses simple documents just for clarity of reading; the usual optional settings can still be used.
See more information on the [bulk page](guide/bulk.md).

## Synchronous Operations

All operations are normally async. To switch to a sync client called .sync on the client object. Then all requests will block until the operations has completed. Eg,
```scala
val resp = client.sync.execute { index into "bands/rock" fields ("name"->"coldplay", "debut"->"parachutes") }
resp.isInstanceOf[IndexResponse] // true
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

Note: Only available for scala 2.10.

For SBT users simply add:

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s" % "1.1.1.0"
```

For Maven users simply add (replace 2.10 with 2.11 for Scala 2.11):

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s_2.10</artifactId>
    <version>1.1.1.0</version>
</dependency>
```

You can always find the latest version on [maven central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.sksamuel.elastic4s%22%20AND%20a%3A%22elastic4s%22)

## Building and Testing

This project is built with SBT. So to build
```
sbt compile
```

And to test
```
sbt test
```

Integration tests run on a locally built elastic that is brought up and torn
down as part of the tests inside your standard /tmp folder. There is no need to configure anything externally.

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
