## Reactive Streams

Elastic4s has an implementation of the [reactive streams](http://www.reactive-streams.org) api for both publishing and subscribing that is built
using Akka. To use this, you need to add a dependency on the elastic4s-streams module.

There are two things you can do with the reactive streams implementation. You can create an elastic subscriber, and have that
stream data from some publisher into elasticsearch. Or you can create an elastic publisher and have documents streamed out to subscribers.


### Dependencies

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
