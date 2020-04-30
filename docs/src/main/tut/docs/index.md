---
layout: docs
title:  "Getting Started"
section: "docs"
---

# Getting Started

We have created sample projects in both sbt, maven and gradle. Check them out here:
https://github.com/sksamuel/elastic4s/tree/master/samples

To get started you will need to add a dependency:

* [elastic4s-client-esjava](https://mvnrepository.com/artifact/com.sksamuel.elastic4s/a:elastic4s-client-esjava)

```scala
// major.minor are in sync with the elasticsearch releases
val elastic4sVersion = "x.x.x"
libraryDependencies ++= Seq(
  // recommended client for beginners
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion,
  // test kit
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test"
)
```

The basic usage is that you create an instance of a client and then invoke the `execute` method with the requests you
want to perform. The execute method is asynchronous and will return a standard Scala `Future[T]`
(or use one of the [Alternative executors](#alternative-executors)) where T is the response
type appropriate for your request type. For example a _search_ request will return a response of type `SearchResponse`
which contains the results of the search.

To create an instance of the HTTP client, use the `ElasticClient` companion object methods.
Requests are created using the elastic4s DSL. For example to create a search request, you would do:

```scala
search("index").query("findthistext")
```

The DSL methods are located in the `ElasticDsl` trait which needs to be imported or extended.

```scala
import com.sksamuel.elastic4s.ElasticDsl._
```

One final import is required if you are using the HTTP client. The API needs a way to unmarshall the JSON response from
the elastic server into the strongly typed case classes used by the API. Rather than bringing in a JSON library of our
choosing and potentially causing dependency issues (or simply bloat), the client expects an implicit `JsonFormat`
implementation.

Elastic4s provides several out of the box (or you can roll your own) JSON serializers and deserializers. The provided
implementations are

- [elastic4s-circe](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-circe)
- [elastic4s-jackson](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-jackson)
- [elastic4s-json4](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json4)
- [elastic4s-play-json](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-play-json)
- [elastic4s-spray-json](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-spray-json)

For example, to use the jackson implementation, add the module to your build and then add this import:


### Connecting to a Cluster

To connect to a standalone ElasticSearch cluster, pass a `JavaClient` to an `ElasticClient`. You can specify protocol,
host, and port in a single string.

```scala
val client = ElasticClient(JavaClient(ElasticProperties("http://host1:9200")))
```

For multiple nodes you can pass a comma-separated list of endpoints in a single string:

```scala
val nodes ="http://host1:9200,http://host2:9200,http://host3:9200"
val client = ElasticClient(JavaClient(ElasticProperties(nodes)))
```

### Using different clients

It is possible to use [alternative clients](https://search.maven.org/search?q=g:com.sksamuel.elastic4s%20elastic4s-client)
in order to connect to a cluster, such as Akka HTTP:

Add the following to your `built.sbt`, replace `x.x.x` with your version of ElasticSearch:

```scala
libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s-client-akka_2.13" % "x.x.x"
```

And then pass `AkkaHttpClient` to the object `ElasticClient.apply` method:

```scala
val client = ElasticClient(AkkaHttpClient("http://host1:9200"))
```

## Example Application

An example is worth 1000 characters so here is a quick example of how to connect to a node with a client, create an
index and index a one field document. Then we will search for that document using a simple text query.

**Note:** As of version `0.7.x` the `LocalNode` functionality has been removed. It is recommended that you stand up
a local ElasticSearch Docker container for development. This is the same strategy used in the [tests](https://github.com/sksamuel/elastic4s/blob/master/elastic4s-testkit/src/main/scala/com/sksamuel/elastic4s/testkit/DockerTests.scala).

```scala
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, ElasticProperties}

object ArtistIndex extends App {

  // in this example we create a client to a local Docker container at localhost:9200
  val client = ElasticClient(JavaClient(ElasticProperties(s"http://${sys.env.getOrElse("ES_HOST", "127.0.0.1")}:${sys.env.getOrElse("ES_PORT", "9200")}")))

  // we must import the dsl
  import com.sksamuel.elastic4s.ElasticDsl._

  // Next we create an index in advance ready to receive documents.
  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block
  // the calling thread but is useful when testing
  client.execute {
    createIndex("artists").mapping(
        properties(
          textField("name")
        )
    )
  }.await

  // Next we index a single document which is just the name of an Artist.
  // The RefreshPolicy.Immediate means that we want this document to flush to the disk immediately.
  // see the section on Eventual Consistency.
  client.execute {
    indexInto("artists").fields("name" -> "L.S. Lowry").refresh(RefreshPolicy.Immediate)
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

