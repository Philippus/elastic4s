---
layout: docs
title:  "Getting Started"
section: "docs"
---

# Getting Started


To get started you will need to add a dependency to either

* [elastic4s-http](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-http)
* [elastic4s-tcp](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-tcp) 

depending on which client you intend you use(or both).

The basic usage is that you create an instance of a client and then invoke the `execute` method with the requests you
want to perform. The execute method is asynchronous and will return a standard Scala `Future[T]` where T is the response
type appropriate for your request type. For example a _search_ request will return a response of type `SearchResponse`
which contains the results of the search.

To create an instance of the HTTP client, use the `HttpClient` companion object methods. To create an instance of the 
TCP client, use the `TcpClient` companion object methods. Requests are the same for either client, but response classes
may vary slightly as the HTTP response classes model the returned JSON whereas the TCP response classes wrap the Java 
client classes.

Requests, such as inserting a document, searching, creating an index, and so on, are created using the DSL syntax that 
is similar in style to SQL queries. For example to create a search request, you would do:

```scala
search("index" / "type") query "findthistext"`
```

The DSL methods are located in the `ElasticDsl` trait which needs to be imported or extended. Although the syntax is 
identical whether you use the HTTP or TCP client, you must import the appropriate trait
(`com.sksamuel.elastic4s.ElasticDSL` for TCP or `com.sksamuel.elastic4s.http.ElasticDSL` for HTTP) depending on which
client you are using.

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
 
For example, to use the jackson implementation, add the module to your build and then add this import:


## SBT Setup

```scala
// major.minor are in sync with the elasticsearch releases
val elastic4sVersion = "x.x.x"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  // for the tcp client
  "com.sksamuel.elastic4s" %% "elastic4s-tcp" % elastic4sVersion,
  
  // for the http client
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  
  // if you want to use reactive streams
  "com.sksamuel.elastic4s" %% "elastic4s-streams" % elastic4sVersion,
  
  // a json library
  "com.sksamuel.elastic4s" %% "elastic4s-jackson" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-play-json" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-circe" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json4s" % elastic4sVersion,
  
  // testing
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test",
  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
)
```

## Your first request

```tut:invisible
import java.io.IOException
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

// setup a home directory
val clusterName: String = "getting-started-with-elastic4s"
val homePath: Path = Files.createTempDirectory(clusterName)
```

All examples use a local node for demonstration purpose. You can start an embedded node with

```tut:silent
import com.sksamuel.elastic4s.embedded.LocalNode

import scala.concurrent.Await
import scala.concurrent.duration._

val localNode = LocalNode(clusterName, homePath.toAbsolutePath.toString)
val client = localNode.elastic4sclient()
```

You can import the DSL and a default execution context with and execute a simple cluster state request with

```tut:book
import com.sksamuel.elastic4s.ElasticDsl._

// await is a helper method to make this operation synchronous instead of async
// You would normally avoid doing this in a real program as it will block your thread
val response = client.execute {
  clusterState()
}.await

response.getClusterName().value

localNode.close()
```

## Example Application

An example is worth 1000 characters so here is a quick example of how to connect to a node with a client, create and
index and index a one field document. Then we will search for that document using a simple text query.

For this example we will use the `elastic4s-circe` json serializer.


```tut:silent
import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.searches.RichSearchResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.settings.Settings

// circe
import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._ 

case class Artist(name: String)

object ArtistIndex extends App {

    // spawn an embedded node
    val localNode = LocalNode(clusterName, homePath.toAbsolutePath.toString)
    val client = localNode.elastic4sclient()

  // This creates a TcpClient. We don't actually use it in our little demo,
  // because elasticsearch 5.x dropped embedded node support and we use the
  // elastic4s embedded node that don't suppot connecting via tcp
  def tcpClient(): TcpClient = TcpClient.transport(
    Settings.builder().put("cluster.name", clusterName).build(),
    ElasticsearchClientUri(s"elasticsearch://${localNode.ipAndPort}")
  )
  
  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block your thread
  client.execute {
    createIndex("bands").mappings(
       mapping("artist") as(
          stringField("name")
       )
    )
  }.await

  client.execute { 
  	indexInto("bands" / "artists") doc Artist("Coldplay") refresh(RefreshPolicy.IMMEDIATE)
  }.await

  // now we can search for the document we just indexed
  val resp: RichSearchResponse = client.execute { 
    search("bands" / "artists") query "coldplay" 
  }.await
  
  println("---- Search Hit Parsed ----")
  resp.to[Artist].foreach(println)
  
  // pretty print the complete response
  import io.circe.Json
  import io.circe.parser._
  println("---- Response as JSON ----")
  println(decode[Json](resp.original.toString).right.get.spaces2)
  
  localNode.close()
}

```

Now lets run our App on our embedded node.

```tut:book
ArtistIndex.main(Array())
```


```tut:invisible
// clean up cluster
Files.walkFileTree(homePath, new SimpleFileVisitor[Path] {
  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    Files.delete(file)
    FileVisitResult.CONTINUE
  }
  override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
    Files.delete(dir)
    FileVisitResult.CONTINUE
  }
})
```
