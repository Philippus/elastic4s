elastic4s
=========

Elastic4s is a concise, idiomatic, type safe Scala Client for ElasticSearch.
It provides a full Scala DSL to construct your queries and (hopefully!) reducing errors.
Due to its typesafe nature elastic4s is also a good way to learn the options available for any operation,
as your IDE can use the type information to show you what methods are available.
Elastic4s also allows you to index JSON documents from standard
JSON libraries such as Jackson without having to unmarshall.

#### Release

The latest release is 0.90.5.2 which is compatible with elasticsearch 0.90.5

[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)
[![Coverage Status](https://coveralls.io/repos/sksamuel/elastic4s/badge.png?branch=master)](https://coveralls.io/r/sksamuel/elastic4s?branch=master)

## Introduction

The basic format of the DSL is to create requests (eg a search request or delete request)
and pass them in to the execute methods on the client, which returns a response object.
All requests on the standard client are asynchronous.
These methods return a standard Scala 2.10 Future object.
Eg, a search request will return a Future[SearchResponse].
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
For more in depth examples keep reading.


## Syntax

Here is a list of the common operations and the syntax used to create requests. 

For more details on each operation click through to the read me page. For options that are not yet documented,
please refer back to the elasticsearch documentation as the DSL keyword is usually called exactly what the option is called in the Java API.

| Operation | Samuel Normal Form Syntax |
|-----------|----------------|
| Create Index     | ```create index <name> mappings { mappings block> } [optional settings]```|
| Index            | ```index into <index/type> fields { <fieldblock> } [optional settings]``` |
| Get              | ```get id <id> from <index/type> [optional settings]```|
| Count            | ```count from <indexes> [types <types> query <queryblock>]``` |
| Delete by id     | ```delete id <id> in <index/type> [optional settings]```
| Delete by query  | ```delete from <index/type> query { <queryblock> } [optional settings]```
| Search           | ```search in <index/type> query { <queryblock> } filter { <filterblock> } facets { <facetblock> } sort { <sortblock> } ....``` |
| More like this   | ```morelike id <id> in <index/type> { fields <fieldsblock> } [optional settings]``` |
| Update           | ```update id <id> in <index/type> script <script> [optional settings]``` |
| Register Query   | ```<id> into <index> query { <queryblock> }``` |
| Percolate Doc    | ```percolate in <index> { fields <fieldsblock> }``` |

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
client.execute { 
    create index "places" mappings (
        "cities" as (      
            field("id") typed IntegerType,
            field("name") boost 4,
            field("content") analyzer StopAnalyzer
        )
     )
}
```

Then ElasticSearch is configured with those mappings for those fields only. It is still fully dynamic and other fields will be created as needed with default options.

More examples on how to create indexes with elastic4s can be [found here](guide/createindex.md)

#### Analyzers

Elasticsearch allows us to register (create) custom analyzers. For more details [read here](guide/analyzers.md).

#### Indexing

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

There are many additional options we can set such as routing, version, parent, timestamp and op type. See [official documentation](http://www.elasticsearch.org/guide/reference/api/index_/) for additional options.

If you want to index directly from a Jackson JSON document, then elastic4s supports this directly.

```scala
val myJsonDoc = ... // some jackson object
client.execute { index into "electronics/phones" source JacksonSource(myJsonDoc) }
```

Or you can even index objects natively and then by using Jackson the object will be marshalled into JSON. This uses the Scala extension in Jackson and so supports scala collections, options, etc.

```scala
val anyOldObject = ... // anything that extends from AnyRef
client.execute { index into "electronics/phones" source ObjectSource(anyOldObject) }
```

In fact you can write your own "source" conversions by simply creating a class that mixes in the trait Source.

#### Searching

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

#### Get

Sometimes we don't want to search and want to retrieve a document directly from the index by id. In this example we are retrieving the document with id 'coldplay' from the bands/rock index and type.
```scala
client.execute {
    get "coldplay" from "bands/rock"
}
```

See more examples and multiget [here](guide/multiget.md)

#### Deleting

In the rare case that we become tired of a band we might want to remove them. Naturally we wouldn't want to remove Chris Martin and boys so we're going to remove U2 instead.
We think they're a little past their best (controversial). This operation assumes the id of the document is "u2".

```scala
client.delete {
    "bands/rock" -> "u2"
}
```

We can take this a step further by deleting by a query rather than id.
In this sense the delete is very similar to an SQL delete statement.
In this example we're deleting all bands where their type is rap.

```scala
client.delete {
    "bands" types "rock" where termQuery("type", "rap")
}
```

See more about delete on the [delete page](guide/delete.md)

#### More like this

If you want to return documents that are "similar" to   a current document we can do that very easily with the more like this query.

```scala
client.execute {
    morelike id 4 from "beers/lager" percentTermsToMatch 0.5
}
```

For all the options see [here](http://www.elasticsearch.org/guide/reference/query-dsl/mlt-query/).

#### Bulk Operations

ElasticSearch is fast. Roundtrips are not.
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

#### Other

There are other DSLs in play. Validate, update, percolate, and explain all have a DSL that is very easy to understand and can be understood from the source. They work in similar ways to the others. Examples will be added in due course.

#### Synchronous Operations

All operations are normally async. To switch to a sync client called .sync on the client object. Then all requests will block until the operations has completed. Eg,
```scala
val resp = client.sync.index { index into "bands/rock" fields ("name"->"coldplay", "debut"->"parachutes") }
resp.isInstanceOf[IndexResponse] // true
```

#### DSL Completeness

As it stands the Scala DSL covers all of the common operations - index, create, delete, delete by query, search, validate, percolate, update, explain, get, and bulk operations. There is good support for the various settings for each of these - more so than the Java client provides in the sense that more settings are provided in a type safe manner. 

However there are settings and operations (mostly admin / cluster related) that the DSL does not yet cover (pull requests welcome!). In these cases it is necessary to drop back to the Java API. This can be done by calling .java on the client object to get the underlying java elastic client, or .admin to get the admin based client, eg, the following request is a Java API request.

```scala
client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases where the Scala DSL is missing a construct, or where there is no need to provide a DSL.

## Using Elastic4s in your project

For SBT users simply add:

```scala
libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s_2.10" % "0.90.5.2"
```

For Maven users simply add:

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s_2.10</artifactId>
    <version>0.90.5.2</version>
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

Integration tests run on a locally built elastic that is brought up and torn down as part of the tests inside your standard /tmp folder. There is no need to configure anything externally.

## Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Stephen Samuel

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
