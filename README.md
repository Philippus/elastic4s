elastic4s - Elasticsearch Scala Client
=========

[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png?branch=master)](https://travis-ci.org/sksamuel/elastic4s)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11.svg?label=latest%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12.svg?label=latest%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)

Elastic4s is a concise, idiomatic, reactive, type safe Scala client for Elasticsearch. The official Elasticsearch Java client can of course be used in Scala, but due to Java's syntax it is more verbose and it naturally doesn't support classes in the core Scala core library nor Scala idioms such as typeclass support.

Elastic4s's DSL allows you to construct your requests programatically, with syntactic and semantic errors manifested at compile time, and uses standard Scala futures to enable you to easily integrate into an asynchronous workflow. The aim of the DSL is that requests are written in a builder-like way, while staying broadly similar to the Java API or Rest API. Each request is an immutable object, so you can create requests and safely reuse them, or further copy them for derived requests. Because each request is strongly typed your IDE or editor can use the type information to show you what operations are available for any request type.

Elastic4s supports Scala collections so you don't have to do tedious conversions from your Scala domain classes into Java collections. It also allows you to index and read classes directly using typeclasses so you don't have to set fields or json documents manually. These typeclasses are generated using your favourite json library - modules exist for Jackson, Circe, Json4s, PlayJson and Spray Json. The client also uses standard Scala durations to avoid the use of strings or primitives for duration lengths.

Read [the full documentation](https://sksamuel.github.io/elastic4s/docs/) to learn more about elastic4s.

#### Key points

* Type safe concise DSL
* Integrates with standard Scala futures or other effects libraries
* Uses Scala collections library over Java collections
* Returns `Option` where the java methods would return null
* Uses Scala `Duration`s instead of strings/longs for time values
* Supports typeclasses for indexing, updating, and search backed by Jackson, Circe, Json4s, PlayJson and Spray Json implementations
* Provides [reactive-streams](#elastic-reactive-streams) implementation
* Provides a testkit subproject ideal for your tests

## Introduction

Elasticsearch (on the JVM) has two interfaces. One is the regular HTTP interface available on port 9200 (by default) and the other is a TCP interface on port 9300 (by default). Historically the Java API provided by Elasticsearch has always been TCP based with the rationale that it saves marshalling requests into JSON and is cluster aware and so can route requests to the correct node. Therefore elastic4s was also TCP based since it delegates requests to the underlying Java client.

Starting with elastic4s 5.2.x a new [HTTP client](https://github.com/sksamuel/elastic4s/tree/master/elastic4s-http) has been added which relies on the Java REST client for connection management, but still uses the familiar elastic4s DSL to build the queries so you don't have to. As of version 5.4.x the HTTP client is now considered production ready after extensive testing on the 5.2 and 5.3 release chains.

Depending on which client you use, you will need to add either `elastic-http` or `elastic-tcp` dependencies to your build.

#### Release

Elastic4s is released for both Scala 2.11 and Scala 2.12. Scala 2.10 support has been dropped starting with the 5.0.x release train. For releases that are compatible with earlier versions of Elasticsearch,
[search maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22).
For more information read [Using Elastic4s in your project](#using-elastic4s-in-your-project).

Starting from version 5.0.0, the underlying Elasticsearch TCP Java client has dependencies on Netty, Lucene and others that it does not bring in transitively. The elastic4s tcp client brings in the dependencies for you, but in case anything is missed, you would need to add it to your build yourself.

The second issue is that it uses Netty 4.1. However some popular projects such as Spark and Play currently use 4.0 and there is a breaking change between the two versions. Therefore if you bring in elastic4s tcp (or even just the elasticsearch Java TCP client) you will get `NoSuchMethodException`s if you try to use it with Play or Spark. I am unaware of a workaround at present, until Spark and Play update to the latest version, other than switching to the HTTP client.

| Elasticsearch Version | Scala 2.10 | Scala 2.11 | Scala 2.12 |
|-------|---------|---------|-----------|
|6.5.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.5.svg?label=latest%206.5%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.5.svg?label=latest%206.5%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|6.4.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.4.svg?label=latest%206.4%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.4.svg?label=latest%206.4%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|6.3.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.3.svg?label=latest%206.3%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.3.svg?label=latest%206.3%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|6.2.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.2.svg?label=latest%206.2%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.2.svg?label=latest%206.2%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|6.1.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.1.svg?label=latest%206.1%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.1.svg?label=latest%206.1%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|6.0.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/6.0.svg?label=latest%206.0%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/6.0.svg?label=latest%206.0%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.6.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.6.svg?label=latest%205.6%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.6.svg?label=latest%205.6%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.5.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.5.svg?label=latest%205.5%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.5.svg?label=latest%205.5%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.4.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.4.svg?label=latest%205.4%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.4.svg?label=latest%205.4%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.3.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.3.svg?label=latest%205.3%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.3.svg?label=latest%205.3%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.2.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.2.svg?label=latest%205.2%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.2.svg?label=latest%205.2%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.1.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.1.svg?label=latest%205.1%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.1.svg?label=latest%205.1%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|5.0.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/5.0.svg?label=latest%205.0%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/5.0.svg?label=latest%205.0%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|
|2.4.x||[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/2.4.svg?label=latest%202.4%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)||
|2.3.x|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10/2.3.svg?label=latest%202.3%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/2.3.svg?label=latest%202.3%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)||
|2.2.x|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10/2.2.svg?label=latest%202.2%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/2.2.svg?label=latest%202.2%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)||
|2.1.x|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10/2.1.svg?label=latest%202.1%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/2.1.svg?label=latest%202.1%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)||
|2.0.x|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10/2.0.svg?label=latest%202.0%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/2.0.svg?label=latest%202.0%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)||
|1.7.x|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.10/1.7.svg?label=latest%201.7%20release%20for%202.10"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.10%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.11/1.7.svg?label=latest%201.7%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.11%22)|[<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/1.7.svg?label=latest%201.7%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)|

For release prior to 2.0 search maven central.


See full [changelog](#changelog).

## Quick Start

We have created sample projects for http, tcp in both sbt, maven and gradle. Check them out here:
https://github.com/sksamuel/elastic4s/tree/master/samples

To get started you will need to add a dependency to either

* [elastic4s-http](https://mvnrepository.com/artifact/com.sksamuel.elastic4s/elastic4s-http)
* [elastic4s-tcp](https://mvnrepository.com/artifact/com.sksamuel.elastic4s/elastic4s-tcp)

depending on which client you intend you use (or both).

The basic usage is that you create an instance of a client and then invoke the `execute` method with the requests you
want to perform. The execute method is asynchronous and will return a standard Scala `Future[T]`
(or use one of the [Alternative executors](#alternative-executors)) where T is the response
type appropriate for your request type. For example a _search_ request will return a response of type `SearchResponse`
which contains the results of the search.

To create an instance of the HTTP client, use the `HttpClient` companion object methods. To create an instance of the
TCP client, use the `TcpClient` companion object methods. Requests are the same for either client, but response classes
may vary slightly as the HTTP response classes model the returned JSON whereas the TCP response classes wrap the Java
client classes.

Requests are created using the elastic4s DSL. For example to create a search request, you would do:

```scala
search("index" / "type").query("findthistext")
```

The DSL methods are located in the `ElasticDsl` trait which needs to be imported or extended. Although the syntax is
identical whether you use the HTTP or TCP client, you must import the appropriate trait
(`com.sksamuel.elastic4s.ElasticDsl` for TCP or `com.sksamuel.elastic4s.http.ElasticDsl` for HTTP) depending on which
client you are using.

### Alternative Executors
The default `Executor` uses scala `Future`s to execute requests, but there are alternate Executors that can be used by
adding appropriate imports. The imports will create an implicit `Executor[F]` and a `Functor[F]`,
where `F` is some effect type.

#### Cats-Effect IO
`import com.sksamuel.elastic4s.cats.effect.instances._` will provide implicit instances for `cats.effect.IO`

#### Monix Task
`import com.sksamuel.elastic4s.monix.instances._` will provide implicit instances for `monix.eval.Task`

#### Scalaz Task
`import com.sksamuel.elastic4s.scalaz.instances._` will provide implicit instances for `scalaz.concurrent.Task` 

### Example SBT Setup

```scala
// major.minor are in sync with the elasticsearch releases
val elastic4sVersion = "x.x.x"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,

  // for the http client
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,

  // if you want to use reactive streams
  "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % elastic4sVersion,

  // testing
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test",
  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
)
```

### Example Application

An example is worth 1000 characters so here is a quick example of how to connect to a node with a client, create an
index and index a one field document. Then we will search for that document using a simple text query.

```scala
import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.embedded.LocalNode
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess}

object ArtistIndex extends App {

  // spawn an embedded node for testing
  val localNode = LocalNode("mycluster", "/tmp/datapath")

  // in this example we create a client attached to the embedded node, but
  // in a real application you would provide the HTTP address to the ElasticClient constructor.
  val client = localNode.client(shutdownNodeOnClose = true)

  // we must import the dsl
  import com.sksamuel.elastic4s.http.ElasticDsl._

  // Next we create an index in advance ready to receive documents.
  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block
  // the calling thread but is useful when testing
  client.execute {
    createIndex("artists").mappings(
      mapping("modern").fields(
        textField("name")
      )
    )
  }.await

  // Next we index a single document which is just the name of an Artist.
  // The RefreshPolicy.Immediate means that we want this document to flush to the disk immediately.
  // see the section on Eventual Consistency.
  client.execute {
    indexInto("artists" / "modern").fields("name" -> "L.S. Lowry").refresh(RefreshPolicy.Immediate)
  }.await

  // now we can search for the document we just indexed
  val resp = client.execute {
    search("artists") query "lowry"
  }.await

  // resp is a Response[+U] ADT consisting of either a RequestFailure containing the
  // Elasticsearch error details, or a RequestSuccess[U] that depends on the type of request.
  // In this case it is a RequestSuccess[SearchResponse]

  println("---- Search Results ----")
  resp match {
    case failure: RequestFailure => println("We failed " + failure.error)
    case results: RequestSuccess[SearchResponse] => println(results.result.hits.hits.toList)
    case results: RequestSuccess[_] => println(results.result)
  }

  // Response also supports familiar combinators like map / flatMap / foreach:
  resp foreach (search => println(s"There were ${search.totalHits} total hits"))

  client.close()
}
```

## Eventual Consistency

Elasticsearch is eventually consistent. This means when you index a document it is not normally immediately available to be searched,
but queued to be flushed to the indexes on disk. By default flushing occurs every second but this can be reduced (or increased) for bulk inserts.
Another option, which you saw in the quick start guide, was to set the refresh policy to `IMMEDIATE` which will force a flush straight away.
You shouldn't use IMMEDIATE for heavy loads as you'll cause contention with elastic constantly flushing to disk.

For more in depth examples keep reading.

## Syntax

Here is a list of the common requests and the syntax used to create them and whether they are supported by the TCP or HTTP client. If the HTTP client does not support them, you will need to fall back to the TCP, or use the Java client and build the JSON yourself. Or even better, raise a PR with the addition. For more details on each request click
through to the readme page. For options that are not yet documented, refer to the Elasticsearch documentation asthe DSL closely mirrors the standard Java API / REST API.

| Operation                                 | Syntax | HTTP | TCP |
|-------------------------------------------|--------|------|-----|
| [Add Alias]                               | `addAlias(alias, index)`                  | yes | yes |
| [Bulk]                                    | `bulk(query1, query2, query3...)`         | yes | yes |
| Cancel Tasks                              | `cancelTasks(<nodeIds>)`                  | yes | yes |
| Cat Aliases                               | `catAliases()`                            | yes | |
| Cat Allocation                            | `catAllocation()`                         | yes | |
| Cat Counts                                | `catCount()` or `catCount(<indexes>`      | yes | |
| Cat Indices                               | `catIndices()`                            | yes | |
| Cat Master                                | `catMaster()`                             | yes | |
| Cat Nodes                                 | `catNodes()`                              | yes | |
| Cat Plugins                               | `catPlugins()`                            | yes | |
| Cat Segments                              | `catSegments(indices)`                    | yes | |
| Cat Shards                                | `catShards()`                             | yes | |
| Cat Thread Pools                          | `catThreadPool()`                         | yes | |
| Clear index cache                         | `clearCache(<index>)`                     | yes | yes |
| Close index                               | `closeIndex(<name>)`                      | yes | yes |
| Cluster health                            | `clusterHealth()`                         | yes | yes |
| Cluster stats                             | `clusterStats()`                          | yes | yes |
| [Create Index]                            | `createIndex(<name>).mappings( mapping(<name>).as( ... fields ... ) )`| yes  | yes |
| [Create Repository]                       | `createRepository(name, type)`            | yes | yes |
| [Create Snapshot]                         | `createSnapshot(name, repo)`              | yes | yes |
| Create Template                           | `createTemplate(<name>).pattern(<pattern>).mappings {...}`| yes | yes |
| [Delete by id]                            | `deleteById(index, type, id)`             | yes | yes |
| Delete by query                           | `deleteByQuery(index, type, query)`       | yes | yes |
| [Delete index]                            | `deleteIndex(index) [settings]`           | yes | yes |
| [Delete Snapshot]                         | `deleteSnapshot(name, repo)`              | yes | yes |
| Delete Template                           | `deleteTemplate(<name>)`                  | yes | yes |
| Document Exists                           | `exists(id, index, type)`                 | yes | |
| [Explain]                                 | `explain(<index>, <type>, <id>)`          | yes | yes |
| Field stats                               | `fieldStats(<indexes>)`                   |     | yes |
| Flush Index                               | `flushIndex(<index>)`                     | yes | yes |
| [Force Merge]                             | `forceMerge(<indexes>)`                   | yes | yes |
| [Get]                                     | `get(index, type, id)`                    | yes | yes |
| Get All Aliases                           | `getAliases()`                            | yes | yes |
| Get Alias                                 | `getAlias(<name>).on(<index>)`            | yes | yes |
| Get Mapping                               | `getMapping(<index> / <type>)`            | yes | yes |
| Get Segments                              | `getSegments(<indexes>)`                  | yes | yes |
| Get Snapshot                              | `getSnapshot(name, repo)`                 | yes | yes |
| Get Template                              | `getTemplate(<name>)`                     | yes | yes |
| [Index]                                   | `indexInto(<index> / <type>).doc(<doc>)`  | yes | yes |
| Index exists                              | `indexExists(<name>)`                     | yes | yes |
| Index stats                               | `indexStats(indices)`                     | yes | |
| List Tasks                                | `listTasks(nodeIds)`                      | yes | yes |
| Lock Acquire                              | `acquireGlobalLock()`                     | yes | |
| Lock Release                              | `releaseGlobalLock()`                     | yes | |
| [Multiget]                                | `multiget( get(1).from(<index> / <type>), get(2).from(<index> / <type>) )` |  yes | yes |
| [Multisearch]                             | `multi( search(...), search(...) )`       | yes | yes |
| Node Info                                 | `nodeInfo(<optional node list>`           | yes | |
| Node Stats                                | `nodeStats(<optional node list>).stats(<stats>`| yes | |
| Open index                                | `openIndex(<name>)`                       | yes | yes |
| Put mapping                               | `putMapping(<index> / <type>) as { mappings block }` | yes | yes |
| Recover Index                             | `recoverIndex(<name>)`                    | yes | yes |
| Refresh index                             | `refreshIndex(<name>)`                    | yes | yes |
| Register Query                            | `register(<query>).into(<index> / <type>, <field>)` |   | yes |
| [Remove Alias]                            | `removeAlias(<alias>).on(<index>)`        | yes | yes |
| [Restore Snapshot]                        | `restoreSnapshot(name, repo)`             | yes | yes |
| Rollover                                  | `rolloverIndex(alias)`                    | yes | |
| [Search]                                  | `search(index).query(<query>)`            | yes | yes |
| Search scroll                             | `searchScroll(<scrollId>)`                | yes | yes |
| Shrink Index                              | `shrinkIndex(source, target)`             | yes | |
| Term Vectors                              | `termVectors(<index>, <type>, <id>)`      | yes | yes |
| Type Exists                               | `typesExists(<types>) in <index>`         | yes | yes |
| [Update By Id]                            | `updateById(index, type, id)`             | yes | yes |
| Update by query                           | `updateByQuery(index, type, query)`       | yes | yes |
| [Validate]                                | `validateIn(<index/type>).query(<query>)` | yes | yes |

Please also note [some java interoperability notes](https://sksamuel.github.io/elastic4s/docs/misc/javainterop.html).


## Connecting to a Cluster

To connect to a stand alone elasticsearch cluster we use the methods on the HttpClient or TcpClient companion objects.
For example, `TcpClient.transport` or `HttpClient.apply`. These methods accept an instance of `ElasticsearchClientUri`
which specifies the host, port and cluster name of the cluster. The cluster name does not need to be specified if it is the
default, which is "elasticsearch" but if you changed it you must specify it in the uri.

Please note that the TCP interface uses port 9300 and HTTP uses 9200 (unless of course you have changed these in your cluster).

Here is an example of connecting to a TCP cluster with the standard settings.

```scala
val client = TcpClient.transport(ElasticsearchClientUri("host1", 9300))
```

For multiple nodes it's better to use the elasticsearch client uri connection string.
This is in the format `"elasticsearch://host1:port2,host2:port2,...?param=value&param2=value2"`. For example:

```scala
val uri = ElasticsearchClientUri("elasticsearch://foo:1234,boo:9876?cluster.name=mycluster")
val client = TcpClient.transport(uri)
```

If you need to pass settings to the client, then you need to invoke `transport` with a settings object.
For example to specify the cluster name (if you changed the default then you must specify the cluster name).

```scala
import org.elasticsearch.common.settings.Settings
val settings = Settings.builder().put("cluster.name", "myClusterName").build()
val client = TcpClient.transport(settings, ElasticsearchClientUri("elasticsearch://somehost:9300"))
```

If you already have a handle to a Node in the Java API then you can create a client from it easily:
```scala
val node = ... // node from the java API somewhere
val client = TcpClient.fromNode(node)
```

Here is an example of connecting to a HTTP cluster.

```scala
val client = HttpClient(ElasticsearchClientUri("localhost", 9200))
```

The http client internally uses the Apache Http Client, which we can customize by passing in two callbacks.

```scala
val client = HttpClient(ElasticsearchClientUri("localhost", 9200), new RequestConfigCallback {
    override def customizeRequestConfig(requestConfigBuilder: Builder) = ...
    }
  }, new HttpClientConfigCallback {
    override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder) = ...
  })
```

## X-Pack-Security

Elastic4s also supports the xpack-security add on (TCP client only). To use this, add the `elastic-xpack-security` dependency to your build and create a client using the `XPackElasticClient` object instead of the `ElasticClient` object. Eg,

scala
```
val client = XPackElasticClient(settings, uri, <plugins>...)
```

If you are using SBT then you might need to add a resolver to the elasticsearch repo.

scala
```
resolvers += "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
```

## Embedded Node

A locally configured node and client can be created by including the elastic4s-embedded module. Then a local node can be started by invoking `LocalNode()` with the cluster name and data path. From the local node we can return a handle to the client by invoking the `client` function.

```scala
import com.sksamuel.elastic4s.embedded.LocalNode
val node = LocalNode(clusterName, pathHome)
val client = node.client(shutdownNodeOnClose = true)
```

To specify settings for the local node you can pass in a settings object like this:
```scala
val settings = Settings.builder()
      .put("cluster.name", "elasticsearch")
      .put("path.home", "mypath")
      .put("http.enabled", false)
      .build()
val node = LocalNode(settings)
val client = node.client(<shutdownNodeOnClose>)
```

If `shutdownNodeOnClose` is true, then once close is called on the client, the local node will be stopped. Otherwise you will manage the lifecycle of the local node yourself (stopping it before exiting the process).

## Create Index

All documents in Elasticsearch are stored in an index. We do not need to tell Elasticsearch in advance what an index
will look like (eg what fields it will contain) as Elasticsearch will adapt the index dynamically as more documents are added, but we must at least create the index first.

To create an index called "places" that is fully dynamic we can simply use:

```scala
client.execute { createIndex("places") }
```

We can optionally set the number of shards and / or replicas

```scala
client.execute { createIndex("places") shards 3 replicas 2 }
```

Sometimes we want to specify the properties of the fields in the index in advance.
This allows us to manually set the type of the field (where Elasticsearch might infer something else) or set the analyzer used,
or multiple other options

To do this we add mappings:

```scala
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.analyzers.StopAnalyzer

client.execute {
  createIndex("places") mappings (
    mapping("cities") as (
      keywordField("id"),
      textField("name") boost 4,
      textField("content") analyzer StopAnalyzer
    )
  )
}
```

Then Elasticsearch is configured with those mappings for those fields only.
It is still fully dynamic and other fields will be created as needed with default options. Only the fields specified will have their type preset.

More examples on the create index syntax can be [found here](https://sksamuel.github.io/elastic4s/docs/indices/createindex.html).

## Analyzers

Analyzers control how Elasticsearch parses the fields for indexing. For example, you might decide that you want
whitespace to be important, so that "band of brothers" is indexed as a single "word" rather than the default which is
to split on whitespace. There are many advanced options available in analayzers. Elasticsearch also allows us to create
custom analyzers. For more details [read about the DSL support for analyzers](https://sksamuel.github.io/elastic4s/docs/misc/analyzers.html).

## Indexing

To index a document we need to specify the index and type and optionally we can set an id.
If we don't include an id then elasticsearch will generate one for us.
We must also include at least one field. Fields are specified as standard tuples.

```scala
client.execute {
  indexInto("places" / "cities") id "uk" fields (
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

## Indexing Typeclass

Sometimes it is useful to index directly from your domain model, and not have to create maps of fields inline. For this
elastic4s provides the `Indexable` typeclass. Simply provide an implicit instance of `Indexable[T]` in scope for any
class T that you wish to index, and then you can use `doc(t)` on the index request. For example:

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
  indexInto("gameofthrones" / "characters").doc(jonsnow)
}
```

Some people prefer to write typeclasses manually for the types they need to support. Other people like to just have
it done automagically. For those people, elastic4s provides extensions for the well known Scala Json libraries that
can be used to generate Json generically.

Simply add the import for your chosen library below and then with those implicits in scope, you can now pass any type
 you like to `doc` and an Indexable will be derived automatically.

| Library | Elastic4s Module | Import |
|---------|------------------|--------|
|[Jackson](https://github.com/FasterXML/jackson-module-scala)|[elastic4s-jackson](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-jackson)|import ElasticJackson.Implicits._|
|[Json4s](http://json4s.org/)|[elastic4s-json4s](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json4s)|import ElasticJson4s.Implicits._|
|[Circe](https://github.com/travisbrown/circe)|[elastic4s-circe](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-circe)|import io.circe.generic.auto._ <br/>import com.sksamuel.elastic4s.circe._|
|[PlayJson](https://github.com/playframework/play-json)|[elastic4s-play-json](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-play-json)|import com.sksamuel.elastic4s.playjson._|
|[Spray Json](https://github.com/spray/spray-json)|[elastic4s-spray-json](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-spray-json)|import com.sksamuel.elastic4s.sprayjson._|

## Searching

Searching is naturally the most involved operation.
There are many ways to do [searching in Elasticsearch](http://www.elasticsearch.org/guide/reference/api/search/) and that is reflected
in the higher complexity of the query DSL.

To do a simple text search, where the query is parsed from a single string
```scala
search("places" / "cities").query("London")
```

That is actually an example of a SimpleStringQueryDefinition. The string is implicitly converted to that type of query.
It is the same as specifying the query type directly:

```scala
search("places" / "cities").query(simpleStringQuery("London"))
```

The simple string example is the only time we don't need to specify the query type.
We can search for everything by not specifying a query at all.
```scala
search("places" / "cities")
```

We might want to limit the number of results and / or set the offset.
```scala
search("places" / "cities") query "paris" start 5 limit 10
```

We can search against certain fields only:
```scala
search("places" / "cities") query termQuery("country", "France")
```

Or by a prefix:
```scala
search("places" / "cities") query prefixQuery("country", "France")
```

Or by a regular expression (slow, but handy sometimes!):
```scala
search("places" / "cities") query regexQuery("country", "France")
```

There are many other types, such as range for numeric fields, wildcards, distance, geo shapes, matching.

Read more about search syntax: [Search]
Read about [Multisearch].
Read about [Suggestions].

## HitReader Typeclass

By default Elasticsearch search responses contain an array of `SearchHit` instances which contain things like the id,
index, type, version, etc as well as the document source as a string or map. Elastic4s provides a means to convert these
back to meaningful domain types quite easily using the `HitReader[T]` typeclass.

Provide an implementation of this typeclass, as an in scope implicit, for whatever type you wish to marshall search responses into, and then you can call `to[T]` or `safeTo[T]` on the response.
The difference between to and safeTo is that to will drop any errors and just return successful conversions, whereas safeTo returns
a sequence of `Either[Throwable, T]`.

A full example:

```scala
case class Character(name: String, location: String)

implicit object CharacterHitReader extends HitReader[Character] {
  override def read(hit: Hit): Either[Throwable, Character] = {
    Right(Character(hit.sourceAsMap("name").toString, hit.sourceAsMap("location").toString))
  }
}

val resp = client.execute {
  search("gameofthrones" / "characters").query("kings landing")
}.await // don't block in real code

// .to[Character] will look for an implicit HitReader[Character] in scope
// and then convert all the hits into Characters for us.
val characters: Seq[Character] = resp.to[Character]
```

This is basically the inverse of the `Indexable` typeclass. And just like Indexable, the json modules provide implementations
out of the box for any types. The imports are the same as for the Indexable typeclasses.

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
 get("coldplay").from("bands" / "rock")
}
```

We can get multiple documents at once too. Notice the following multiget wrapping block.

```scala
client.execute {
  multiget(
    get("coldplay").from("bands" / "rock"),
    get("keane").from("bands" / "rock")
  )
}
```

See more [get examples] and usage of [Multiget] here.

## Deleting

In the rare case that we become tired of a band we might want to remove them. Naturally we wouldn't want to remove Chris Martin and boys so we're going to remove U2 instead.
We think they're a little past their best (controversial). This operation assumes the id of the document is "u2".

```scala
client.execute {
  delete("u2").from("bands/rock")
}
```

We can take this a step further by deleting by a query rather than id.
In this example we're deleting all bands where their type is pop.

```scala
client.execute {
  deleteIn("bands").by(termQuery("type", "pop"))
}
```

See more about delete on the [delete page]

## Updates

We can update existing documents without having to do a full index, by updating a partial set of fields.

```scala
client.execute {
  update("25").in("scifi" / "starwars").docAsUpsert (
    "character" -> "chewie",
    "race" -> "wookie"
  )
}
```

For more examples see the [Update] page.

## More like this

If you want to return documents that are "similar" to  a current document we can do that very easily with the more like this query.

```scala
client.execute {
  search("drinks" / "beer") query {
    moreLikeThisQuery("name").likeTexts("coors", "beer", "molson") minTermFreq 1 minDocFreq 1
  }
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
    indexInto("bands" / "rock") fields "name"->"coldplay",
    indexInto("bands" / "rock") fields "name"->"kings of leon",
    indexInto("bands" / "pop") fields (
      "name" -> "elton john",
      "best_album" -> "tumbleweed connection"
    )
  )
}
```
A single HTTP or TCP request is now needed for 4 operations. In addition Elasticsearch can now optimize the requests,
by combinging inserts or using aggressive caching.

The example above uses simple documents just for clarity of reading; the usual optional settings can still be used.
See more information on the [Bulk].

## Json Output

It can be useful to see the json output of requests in case you wish to tinker with the request in a REST client or your browser. It can be much easier to tweak a complicated query when you have the instant feedback of the HTTP interface.

Elastic4s makes it easy to get this json where possible. Simply invoke the `show` method on the client with a request to get back a json string. Eg:

```scala
val json = client.show {
  search("music" / "bands") query "coldplay"
}
println(json)
```

Not all requests have a json body. For example _get-by-id_ is modelled purely by http query parameters, there is no json body to output. And some requests aren't supported by the show method - you will get an implicit not found error during compliation if that is the case

Also, as a reminder, the TCP client does not send JSON to the nodes, it uses a binary protocol, so the provided JSON should be used as a debugging tool only. For the HTTP client the output is exactly what is sent.

## Synchronous Operations

All operations are normally asynchronous. Sometimes though you might want to block - for example when doing snapshots or when creating the initial index. You can call `.await` on any operation to block until the result is ready. This is especially useful when testing.

```scala
val resp = client.execute {
  index("bands" / "rock") fields ("name"->"coldplay", "debut"->"parachutes")
}.await
```

## Search Iterator

Sometimes you may wish to iterate over all the results in a search, without worrying too much about handling futures, and re-requesting
via a scroll. The `SearchIterator` will do this for you, although it will block between requests. A search iterator is just an implementation
of `scala.collection.Iterator` backed by elasticsearch queries.

To create one, use the iterate method on the companion object, passing in the http client, and a search request to execute. The
search request must specify a keep alive value (which is used by elasticsearch for scrolling).

```scala
implicit val reader : HitReader[MyType] =  ...
val iterator = SearchIterator.iterate[MyType](client, search(index).matchAllQuery.keepAlive("1m").size(50))
iterator.foreach(println)
```

For instance, in the above we are bringing back all documents in the index, 50 results at a time, marshalled into
instances of `MyType` using the implicit `HitReader` (see the section on HitReaders). If you want just the raw
elasticsearch `Hit` object, then use `SearchIterator.hits`

Note: Whenever the results in a particular
batch have been iterated on, the `SearchIterator` will then execute another query for the next batch and block waiting on that query.
So if you are looking for a pure non blocking solution, consider the reactive streams implementation. However, if you just want a
quick and simple way to iterate over some data without bringing back all the results at once `SearchIterator` is perfect.

## DSL Completeness

As it stands the Scala DSL covers all of the common operations - index, create, delete, delete by query, search, validate, percolate, update, explain, get, and bulk operations.
There is good support for the various settings for each of these - more so than the Java client provides in the sense that more settings are provided in a type safe manner.

However there are settings and operations (mostly admin / cluster related) that the DSL does not yet cover (pull requests welcome!).
In these cases it is necessary to drop back to the Java API.
This can be done by calling .java on the client object to get the underlying java elastic client,

```scala
client.java.admin.cluster.prepareHealth.setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases
where the Scala DSL is missing a construct, or where there is no need to provide a DSL.

## Elastic Reactive Streams

Elastic4s has an implementation of the [reactive streams](http://www.reactive-streams.org) api for both publishing and subscribing that is built
using Akka. To use this, you need to add a dependency on the elastic4s-streams module.

There are two things you can do with the reactive streams implementation. You can create an elastic subscriber, and have that
stream data from some publisher into elasticsearch. Or you can create an elastic publisher and have documents streamed out to subscribers.

### Integrate

First you have to add an additional dependency to your `build.sbt`

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-streams" % "x.x.x"
```

or

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % "x.x.x"
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
  def request(t: T): BulkCompatibleRequest =  index into "index" / "type" fields ....
}
```
Then the subscriber can be created, and attached to a publisher:

```scala
val subscriber = client.subscriber[SomeType](batchSize, concurrentBatches, () => println "all done")
publisher.subscribe(subscriber)
```

## Using Elastic4s in your project

For gradle users, add (replace 2.12 with 2.11 for Scala 2.11):

```groovy
compile 'com.sksamuel.elastic4s:elastic4s-core_2.12:x.x.x'
```

For SBT users simply add:

```scala
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "x.x.x"
```

For Maven users simply add (replace 2.12 with 2.11 for Scala 2.11):

```xml
<dependency>
    <groupId>com.sksamuel.elastic4s</groupId>
    <artifactId>elastic4s-core_2.12</artifactId>
    <version>x.x.x</version>
</dependency>
```

Check for the latest released versions on [maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22)

## Building and Testing

This project is built with SBT. So to build
```scala
sbt compile
```

And to test
```scala
sbt test
```

For the tests to work you will need to run a local elastic instance on port 9200. One easy way of doing this is to use docker:
`docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "path.repo=/tmp" docker.elastic.co/elasticsearch/elasticsearch-oss:6.1.2` replacing that 6.1.2 with whatever version is current.

## Changelog

###### 6.1.2

* Added rollover API to HTTP
* Added terms set query
* Added DSL aliases for span within and span containing

###### 6.1.1

* Added index stats API
* Added cats segments API
* Fix: support update of sequences and nested fields #1193
* Fix: Address aws normalized uri encoding #1188
* Added raw source in search requests
* Added profile option to search requests
* Add GeoBoundsAggregationBuilder #1189
* Add support for stored scripts #1183
* Refactored geo shape query model to support multiple shapes #1187
* Fix: unchecked matches in XContentBuilder #1184
* Add derivative aggregation builder #1179
* Added explicit 'not implemented' errors to aggregation and query builder functions #1181
* Added terms lookup query support in http client #1182
* Range aggregation support #1175
* Added Bucket script pipeline aggs #1177

###### 6.1.0

* Bumped wire version to elasticseach 6.1.0
* Updated terms agg to support regex  #1174
* Updated range and date queries to use elastic date math #1170
* Wrapping apache client errors in JavaRestClientExceptionWrapper so we know which client caused them #1165
* Added AggReader derivation for Circe module
* Fixed completion suggestion option #1173
* Add multi criteria order in term aggregation #1172
* Added sum bucket agg for http. #1164
* Add ExecutionContext param to execute (with default value) #1167
* Stored scripts fix #1162

###### 6.0.4

* Added to[T] and safeTo[T] on aggregations #1156
* Added _shard, _node, and _routing to search hit #1160
* java.lang.NullPointerException in ElasticError.parse #1159

###### 6.0.3

* Added track_total_hits option to searches
* Added get segment to http api

###### 6.0.2

* Added snapshots and repositories to the http api

###### 6.0.1

* Added document exists api #1155
* Fix partitioned terms aggregation (fixes  #1153)

###### 6.0.0

* HTTP Client should now be the first choice client. The TCP Client has been deprecated as it will be removed in version 7 of elasticsearch itself. See https://www.elastic.co/blog/elasticsearch-5-6-0-released. Specifically, _HTTP client will become the official way for Java applications to communicate with Elasticsearch, replacing the Transport Client, which will be removed in Elasticsearch 7.0._.
* HTTP Client no longer has a dependency on the main elasticsearch jars - no more version clashes (netty!) and a hugely reduced footprint.
* HTTP operations now return Either[RequestFailure, RequestSuccess[U]] for better error handling. Each of `RequestFailure` and `RequestSuccess` contain the http status code, the full json response body, and any http headers, in addition to either the error details or the request resposne type.
* Any methods deprecated before version 5.0.0 have been removed.
* Operations that accept an index and a type have been deprecated in favour of index only operations. This is because Elasticsearch plan to remove types in version 7, and in version 6 you are limited to a single type per index. See - https://www.elastic.co/blog/elasticsearch-6-0-0-alpha1-released
* Deprecated implicit conversion of a tuple to an index/type has been removed. So instead of "index" -> "type", you should use "index" / "type", which has been the default since 2.4.0. Or even better, don't use the type at all anymore, see point above.
* The String field type has been removed, which has been deprecated since 5.0.0. Use textField or keywordField.
* Added doc values to search requests
* Reworked the HTTP aggregation response API to support better types and subaggs
* disableCoord has been removed from bool and common term queries
* Added getIndex request type
* `getAliases` is now overloaded to accept seq of Index and Alias objects to make it clearer how it works. The existing `getAlias` is deprecated.
* date_range, and extended_stats aggregations have been implemented for the http client
* Added aliases when creating indexes
* Removed hard coded defaults from all token filters
* Added suport for new Join Type fields (used by parent / child mappings)
* Http client now url encodes index names and ids
* Added reindex request type
* Added compound word token filter
* Loads of bugs fixes

###### 5.5.4

* Fixed json error when using sort in GeoDistance

###### 5.5.3

* Added Reindex for http

###### 5.5.2

* Added routing and refresh param for update by query in http

###### 5.5.1

* Create UpdateByQuery for http

###### 5.5.0

* First release of 5.5.x

###### 5.4.12

* Timezone in date range fix
* Fixed completion suggestion option
* Completion context mapping
* Moved text for phrase suggestion
* fix inner hits for HasChild (backport of PR 1017)
* Completion suggestion
* include pipeline in index definition for http client
* make nested field optional
* propagate inner hits in has parent query definition
* Add bulk requests '_version' and '_version_type' fields support to elastic4s-http

###### 5.4.11

* Gauss function scorer not working in HTTP Client [5.4.10] #1026

###### 5.4.10

* #1013 elastic4s-http: Fixes handling or TermsOrder in TermsAggregationBuilder

###### 5.4.9

* Adding missing StatsAggregationBuilder to elastic4s-http 5.4.x
* Making HasChildQueryDefinition min/max params each independantly optional in 5.4.x (same as 6.x)
* Add '_parent' to SearchHit

###### 5.4.8

* NestedAggregationBuilder to 5.4.x, fixes #999
* support for CumulativeSum and DateRange in HTTP aggs #996

###### 5.4.7

* Make ParentIdQueryDefinition extend QueryDefinition
* Add analyzers to createTemplate settings object #977

###### 5.4.6

* bulk api should include _routing field #963

###### 5.4.5

* UnmappableCharacterException when operating non-ASCII characters #928. This would cause exceptions if your requests had non-ascii characters and was introduced in 5.4.3.

###### 5.4.4

* TCP Client was not setting types correctly for delete by query #942
* HTTP Update was not setting script correctly #930
* Elasticsearch Client URI now supports SSL for the HTTP client #932
* IndicesOptions was not being set on search correctly for HTTP #943
* More like this query now supports routing on items
* Fixed ScoreMode.TOTAL serialization for HTTP #937
* Upgraded to Elastic 5.4.1 to fix #939

###### 5.4.3

* Can extract aggregations as a JSON string

###### 5.4.2

* Added http streams implementation; available as `elastic-http-streams` module
* Bulk items to show full response in http

###### 5.4.1

* Added get mapping to http
* Added index recovery to http
* Added force merge to http
* Terms agg now supports easier custom ordering #440

###### 5.4.0

* Http Client is now considered production ready and no longer marked as experimental.
* Get all indices with their alias #871
* added cat shards, plugins, nodes, thread pool, health, alloc, aliases, master and indices  #872
* Added search template #894
* FieldsMapper doesn't handle null #899
* Multisearch now sets from/size properly #901
* Add simple category contexts for completion suggestions
* #786 #792 #832 Internal json marshalling now via jackson
* Added match none query #893
* Keywords are docvalued by default
* Implement field collapsing introduced in 5.3.0
* fix StackOverflowError for GeoBoundingBoxQueryDefinition.corners(GeoPoint, GeoPoint)
* In exponential decay score the decay is disregarded when offset was not provided #883
* failureMessageOpt in RichBulkResponse doesn't work as an option #890
* #884 Added global suggestion text

###### 5.3.2

* Fix spray-json method names
* Added support of range aggregation for http client
* Bumped sbt version to 0.13.15
* Bumped Scala version to 2.12.2
* Support inner highlight
* Fixes 'type' and 'matched_fields' for highlight query.
* Added support of HDR percentiles aggregation (tcp only)

###### 5.3.1

* Updated rich search responses to throw exceptions on bad `to`
* client should handle integers in range queries #865
* Added max bucket agg builder for http
* pipeline aggregation regression #838

###### 5.3.0

* Added top hits aggs support to http #857
* Added support for {dynamic_type} in dynamic templates? #519
* Added clearScroll implementation for http client.
* Removing id property as cannot be send to ES anymore.
* DateHistogramAggregation support for HttpClient
* PutMapping implementation in Http client
* Implemented support for spray-json
* use utf-8 for http entities instead of system encoding
* make `RichSearchHit#fieldValueOpt` return an `Option[AnyRef]`
* Enable parallel tests
* DateRangeAggregation support for multiple unbounded ranges
* Added parent support for http client bulk request.
* Remove deprecation warning for FuzzyQuery
* Starting embedded node with `http.enabled: false` crashes with NPE #781
* Object and nested fields broken in 5.2.11 #819
* npe in terms aggregation #828
* Adding publishMicrosite to release process
* Handle http client failures by endpoint
* #821 Fixed omitted `value` field in RegexQuery serialization
* Add raw query support for HTTP
* #565 create index from raw json string
* Added index template to http
* Added span within and span containing queries to tcp
* Added span containing query to http
* Added span not, span within, and span or queries to http
* Added parent id query to http
* Added script query to http
* added geo bounding box query to http

###### 5.2.12

* Add index template support to the HTTP client
* Added span, geo, parent id and script queries to http
* Fixed regression on object field definitions
* Fixed NPE when local node is started with http false
* Fixed error on get alias when index does not exist
* Added `RequestConfigCallback` and `HttpClientConfigCallback` support to the HTTP client

###### 5.2.11

* Added more like this and nested query  to http
* Made `score` available in Hit (for use by HitReader)
* Added addalias, getalias and alias exists for http
* Added cluster state and cluster health for http client
* Added node stats to http
* Added shard store to http
* Updated field definitions to be immutable case classes, adding missing 5.x properties; removed obsolete 2.x properties
* Added support for unified http and tcp test suite
* Added normalizer support
* Minor circe improvements
* Update Index Level support to http
* Added `missing` and `filter` aggregation to http
* Updated range query with missing fields
* Set content type to JSON for all http requests

###### 5.2.10

* Added explain requests to http
* Added tasks api to http
* Removed old multifield syntax, use only the 5.x compatible syntax
* Fixed regression in subaggregations in 5.2.9
* Removed `_id` field as this is no longer customisable
* Added `minmax`, `cardinality` and `value count` aggregations
* Added UUID support to buildable terms query
* Added search iterator for http and restored Iterable Search for tcp

###### 5.2.9

* Initial aggs for http with `terms` and `sum` aggs
* Fixed source filtering on http get

###### 5.2.8

* Highlight support added to Http Client
* Fix for raw query
* Range query and Terms query added to http client
* Elements arg fix in reactive streams
* VersionType added to index requests
* Optional deps fixed for http client

###### 5.2.7

* Fixed bug in hit reader and tuples
* Added flush index, delete index, clear cache to http client
* Added index exists and type exists requests to http client
* Added validate support to http client
* Supporting has_child, has_parent, dismax, fuzzy and boosting query types in http client
* Search scroll added to http client
* Added keyed filters aggregation to TCP client

###### 5.2.6

* Fixed bug with source excludes since 5.2.0
* Fixed HTTPS bug in http client
* Added client.show to TCP client
* Added constant score query suppor to http client

###### 5.2.0

* Supports Elasticsearch 5.2.x
* Added experimental HTTP client

###### 5.1.5

* Fixed filter in add alias

######

* Added com.vividsolutions.jts.geom to build to avoid "stub issues"

###### 5.1.3

* Added explicit search scroll close in reactive streams #695
* Added phrase suggestion collate query

###### 5.1.1

* Upgrade to elasticsearch 5.1.1

###### 5.0.4

* elastic4s-xpack-security module.
* Added missing geoShapeQuery for indexed shape

###### 5.0.3

* Fixed issue with search scroll id using a scala Duration

###### 5.0.2

* Elastic4s now brings in the required netty and lucene dependencies that the `elasticsearch-transport` module needs, but doesn't bring in transitively.
* Added strongly typed listener to `elastic4s-streams`.

###### 5.0.1

* Released `elastic4s-play-json` for Elasticsearch 5.

###### 5.0.0

Elasticsearch 5.0 is a huge release from the people at Elastic. There have been some queries and actions removed completely, and plenty of methods have been renamed or changed. The full breaking changes log in Elasticsearch itself is here:
https://www.elastic.co/guide/en/elasticsearch/reference/current/breaking-changes-5.0.html

These are the majority of changes in the scala client. As part of upgrading, there will certainly be some tweaking required.

* TTL has been removed. As a replacement, you should use time based indexes or cron a delete-by-query with a range query on a timestamp field.
* Scala specific enums have (mostly) been removed in favour of using the Java Enums provided by the Java client.
* Infix notation has (mostly) been deprecated
* New typeclass `HitReader` for reading data from searches. This typeclass handles errors (it returns `Either[Throwable, T]`) and now works for search, get, multisearch and multiget.
* PercolatorQuery has been added (Elasticsearch have removed the previous percolator actions)
* Scala specific re-index has been removed and the elasticsearch re-index action has been added #556
* Update by query is now a plugin in Elasticsearch and so has been added to the client #616
* Delete by query is now a plugin in Elasticsearch and so has been added to the client #616
* Rollover has been added #658
* Suggestions action has been removed in Elasticsearch, suggestions are now available on the search action
* Search API has methods for getting the correct suggestion result back, eg `resp.termSuggestion("mysugg1")`
* Multimatchquery now supports boosting individual fields #545
* Existing bool syntax has been deprecated in favour of a more explicit syntax #580
* Terms lookup query has been added #631
* Terms query now supports Iterables #597 #599
* New module added for embedded nodes
* `successFn` has been added to elastic-streams. This is only invoked if there are no errors. #615
* Cluster health supports parameters #600
* Added indices options to GetSettings
* Removed most of the methods deprecated prior to 2.0
* Create template now supports alias #652
* Fixed bug in docAsUpsert #651 #592
* Support predefined Analyzers / Filters #602
* Support for predefined language-specific stopwords #596
* Support geo_shape queries #639
* More like this request has been removed, use more like this query
* Added better name for update in index into #535
* Allow querying mappings for all types in an index #619
* Add shutdown listener to close local node on JVM exit #655


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

## Used By
* Barclays Bank
* HSBC
* Shazaam
* Graphflow
* Hotel Urbano
* Immobilien Scout
* Deutsche Bank
* Goldman Sachs
* HMRC
* Canal+
* AOE
* Starmind

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

[Add Alias]: https://sksamuel.github.io/elastic4s/docs/indices/aliases.html
[Bulk]: https://sksamuel.github.io/elastic4s/docs/document/bulk.html
[Create Index]: https://sksamuel.github.io/elastic4s/docs/indices/createindex.html
[Create Repository]: https://sksamuel.github.io/elastic4s/docs/misc/snapshot.html
[Create Snapshot]: https://sksamuel.github.io/elastic4s/docs/misc/snapshot.html
[Delete by id]: https://sksamuel.github.io/elastic4s/docs/document/delete.html
[Delete index]: https://sksamuel.github.io/elastic4s/docs/document/delete.html
[Delete index]: https://sksamuel.github.io/elastic4s/docs/document/delete.html
[delete page]: https://sksamuel.github.io/elastic4s/docs/document/delete.html
[Delete Snapshot]: https://sksamuel.github.io/elastic4s/docs/misc/snapshot.html
[Explain]: https://sksamuel.github.io/elastic4s/docs/search/explain.html
[Get]: https://sksamuel.github.io/elastic4s/docs/document/get.html
[get examples]: https://sksamuel.github.io/elastic4s/docs/document/get.html
[Index]: https://sksamuel.github.io/elastic4s/docs/document/index.html
[Multiget]: https://sksamuel.github.io/elastic4s/docs/document/multiget.html
[Multisearch]: https://sksamuel.github.io/elastic4s/docs/search/multisearch.html
[Force Merge]: https://sksamuel.github.io/elastic4s/docs/indices/optimize.html
[Remove Alias]: https://sksamuel.github.io/elastic4s/docs/indices/aliases.html
[Restore Snapshot]: https://sksamuel.github.io/elastic4s/docs/misc/snapshot.html
[Search]: https://sksamuel.github.io/elastic4s/docs/search/search.html
[Suggestions]: https://sksamuel.github.io/elastic4s/docs/search/suggestions.html
[Update]: https://sksamuel.github.io/elastic4s/docs/document/update.html
[Validate]: https://sksamuel.github.io/elastic4s/docs/search/validate.html
