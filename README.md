elastic4s
=========

Elastic4s is a concise, idiomatic, type safe Scala Client for ElasticSearch. It provides a full Scala DSL to construct your queries and (hopefully!) reducing errors. Due to its typesafe nature Elastic4s is also a good way to learn the options available for any operation, as your IDE can use the type information to show you what methods are available. Elastic4s also allows you to index JSON documents from standard JSON libraries such as Jackson without having to unmarshall.



[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)
[![Coverage Status](https://coveralls.io/repos/sksamuel/elastic4s/badge.png?branch=master)](https://coveralls.io/r/sksamuel/elastic4s?branch=master)

## Introduction to the DSL

The basic format of the DSL is to create requests (eg a search request or delete request) and pass them in to the execute methods on the client, which returns a response object. 
All requests on the standard client are asynchronous. These methods return a standard Scala 2.10 Future object. Eg, a search request will return a Future[SearchResponse]. The response objects are the same as for the Java API.

All the request methods exist in the ElasticDsl object. The standard client is a class called ElasticClient. To create a client use the constructor methods on the ElasticClient companion object. 

An example is worth 1000 characters so here is a quick example of how to create a client and index a one field document:

```scala
import com.sksamuel.elastic4s.ElasticDsl._

object Test extends App {

  val client = ElasticClient.local()
  client execute { index into "bands/singers" fields "name"->"chris martin" }

}
```
For more in depth examples keep reading.


#### DSL Syntax

Here is a list of the common operations and the syntax used to create requests. For full examples read the following sections. Most operations have many optional settings. For those you should consult the elastic search document for the particular operation. The DSL keyword is usually called exactly what the option is called in the json request.

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

#### Client

A locally configured node and client can be created simply by:

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
val client = ElastiClient.remote("host1", 9300)
// or for multiple nodes
val client = ElastiClient.remote("host1" -> 9300, "host2" -> 9300)
```


If you already have a handle to a Node then you can create a client from it easily:
```scala
val node = ... // node from java somewhere
val client = ElasticClient.fromNode(node)
```


#### Create Index

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
	        "id" typed IntegerType,
            "name" boost 4,
            "content" analyzer StopAnalyzer
        )
     )
}
```

Then ElasticSearch is configured with those mappings for those fields only. It is still fully dynamic and other fields will be created as needed with default options.

More examples on how to create indexes with elastic4s can be [found here](guide/createindex.md)

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
search in "places/cities" query "London"
```

We can search for everything by not specifying a query at all.
```scala
search in "places/cities"
```

We might want to limit the number of results and / or set the offset.
```scala
search in "places/cities" query "paris" start 5 limit 10
```

One of the great features of ElasticSearch is the number of queries it provides. Here we can use the term query to limit the search to just the state of Georgia rather than the country of Georgia.
```scala
search in "places/cities" { term("state", "georgia") }
```

We wouldn't be able to do very much if we couldn't combine queries. So here we combine three queries, 2 "musts" that must match the documents and 1 "not" that must not match the documents. This is what ElasticSearch calls a [boolean query](http://www.elasticsearch.org/guide/reference/query-dsl/bool-query/). You'll see in this example that I don't like to vacation anywhere that is too hot, and I want to only vacation somewhere that is awesome and that where the name ends with 'cester' like Gloucester or Leicester.
```scala
search in "places/cities" query { 
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

We might want to return facets from our search. Naturally in London we'd want to search for historic landmarks and the age of those attractions and so we'd offer these as selectable facets to our lovely users.
```scala
search in "places/cities" query "london" facets (
    facet terms "landmark" field "type",
    facet range "age" field "year" range (1000->1200, 1200->1400, 1600->1800, 1800->2000)
)
```

Other facet types include geo distance, query, filter, range, date, histogram. The full documentation is [here](http://www.elasticsearch.org/guide/reference/api/search/facets/).

Elasticsearch provides [sorting](http://www.elasticsearch.org/guide/reference/api/search/facets/) of course. So does elastic4s. You can even include multiple sorts - rather like multiple order clauses in an SQL query.

```scala
search in "places/cities" query "europe" sort (
    by field "name",
    by field "status"
)
```
Other options provided are highlighting, suggestions, filters, scrolling, index boosts and scripting. See [the query dsl](http://www.elasticsearch.org/guide/reference/api/search/) for more information.

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

ElasticSearch is fast. HTTP is not. Sometimes we want to wrestle every last inch of performance and a useful way to do this is to batch up operations. Elastic has predicted our wishes and created the bulk API. To do this we simply combine index, delete and update operations into a sequence and execute using the bulk method in the client.

```scala
client.bulk {
   index into "bands/rock" fields "name"->"coldplay",
   index into "bands/rock" fields "name"->"kings of leon",
   index into "bands/pop" fields ( 
      "name"->"elton john",
      "best_album"-"goodbye yellow brick road"
   ),
   delete id "taylor swift" from "bands/pop"
}
```
A single HTTP request is now needed for 4 operations. The example above uses simple documents just for clarity of reading; the usual optional settings can still be used.

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
libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s" % "0.90.2.8"
```

For Maven users simply add:

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s</artifactId>
    <version>0.90.2.8</version>
</dependency>
```

You can always find the latest version on [maven central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.sksamuel.elastic4s%22%20AND%20a%3A%22elastic4s%22)

## Building and Testing

Sorry SBT folks but this project is built with maven. To run the unit and integration tests
```
mvn clean test
```
Integration tests run on a locally built elastic that is brought up and torn down as part of the tests inside your standard /tmp folder. There is no need to configure anything externally.

To build and deploy into your local repo
```
mvn clean install
```

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
