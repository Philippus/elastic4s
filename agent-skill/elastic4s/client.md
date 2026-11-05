# Client Setup

## SBT dependency

```scala
// Core (always required)
"com.sksamuel.elastic4s" %% "elastic4s-core" % "<version>"

// Pick ONE client backend:
"com.sksamuel.elastic4s" %% "elastic4s-client-esjava"  % "<version>"  // JavaClient (recommended default)
"com.sksamuel.elastic4s" %% "elastic4s-client-pekko"   % "<version>"  // PekkoHttpClient
"com.sksamuel.elastic4s" %% "elastic4s-client-akka"    % "<version>"  // AkkaHttpClient
"com.sksamuel.elastic4s" %% "elastic4s-client-sttp4"   % "<version>"  // SttpRequestHttpClient (sttp 4.x)
"com.sksamuel.elastic4s" %% "elastic4s-client-sttp"    % "<version>"  // SttpRequestHttpClient (sttp 3.x)
"com.sksamuel.elastic4s" %% "elastic4s-client-http4s"  % "<version>"  // Http4sClient

// Pick ONE JSON backend (needed for serialization):
"com.sksamuel.elastic4s" %% "elastic4s-json-jackson"   % "<version>"
"com.sksamuel.elastic4s" %% "elastic4s-json-circe"     % "<version>"
"com.sksamuel.elastic4s" %% "elastic4s-json-play"      % "<version>"
"com.sksamuel.elastic4s" %% "elastic4s-json-zio"       % "<version>"

// Optional effect integrations:
"com.sksamuel.elastic4s" %% "elastic4s-effect-zio"     % "<version>"
"com.sksamuel.elastic4s" %% "elastic4s-effect-monix"   % "<version>"
"com.sksamuel.elastic4s" %% "elastic4s-effect-scalaz"  % "<version>"
```

---

## JavaClient (default — uses official ES Java HTTP library)

```scala
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.http.JavaClient

// Single node
val client = ElasticClient(JavaClient(ElasticProperties("http://localhost:9200")))

// Multiple nodes (comma-separated)
val client = ElasticClient(JavaClient(ElasticProperties("http://node1:9200,http://node2:9200,http://node3:9200")))

// HTTPS
val client = ElasticClient(JavaClient(ElasticProperties("https://my-cluster.es.io:9243")))
```

### Sniffing variant

```scala
import com.sksamuel.elastic4s.http.JavaClientSniffed

val client = ElasticClient(JavaClientSniffed(ElasticProperties("http://localhost:9200")))
// Automatically discovers all cluster nodes via _nodes API
```

---

## Authentication

Authentication is passed as an implicit `CommonRequestOptions` in scope when calling `client.execute(...)`.

```scala
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions}

// Username / password (Basic Auth)
implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
  authentication = Some(Authentication.UsernamePassword("elastic", "changeme"))
)

// API key
implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
  authentication = Some(Authentication.ApiKey("my-api-key-value"))
)

// Then use normally — options is picked up automatically:
client.execute { search("my-index").query(matchAllQuery()) }
```

> The JavaClient also supports embedding credentials in the URL:
> `ElasticProperties("http://elastic:changeme@localhost:9200")` — but prefer `CommonRequestOptions` for explicit control.

---

## PekkoHttpClient

```scala
import com.sksamuel.elastic4s.pekko.{PekkoHttpClient, PekkoHttpClientSettings}
import org.apache.pekko.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global

implicit val system: ActorSystem = ActorSystem()

val client = ElasticClient(PekkoHttpClient(PekkoHttpClientSettings.default))
// Reads from application.conf key: com.sksamuel.elastic4s.pekko
```

`application.conf` for Pekko:
```hocon
com.sksamuel.elastic4s.pekko {
  hosts: ["localhost:9200"]
  https: false
  verify-ssl-certificate: true
  queue-size: 1000
  blacklist {
    min-duration = 1m
    max-duration = 30m
  }
  max-retry-timeout = 30s
  username = "elastic"      # optional
  password = "changeme"     # optional
}
```

---

## AkkaHttpClient

```scala
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import akka.actor.ActorSystem

implicit val system: ActorSystem = ActorSystem()

val client = ElasticClient(AkkaHttpClient(AkkaHttpClientSettings.default))
```

Same `application.conf` structure as Pekko, but under `com.sksamuel.elastic4s.akka`.

---

## SttpRequestHttpClient (sttp 4.x)

```scala
import com.sksamuel.elastic4s.sttp4.SttpRequestHttpClient
import com.sksamuel.elastic4s.ElasticNodeEndpoint
import sttp.client4.httpurlconnection.HttpURLConnectionBackend

val backend  = HttpURLConnectionBackend()
val endpoint = ElasticNodeEndpoint("http", "localhost", 9200, None)
val client   = ElasticClient(SttpRequestHttpClient(backend, endpoint))
```

---

## Http4sClient (cats-effect IO)

```scala
import com.sksamuel.elastic4s.http4s.Http4sClient
import com.sksamuel.elastic4s.ElasticClient
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.ember.client.EmberClientBuilder

val (emberClient, _) = EmberClientBuilder.default[IO].build.allocated.unsafeRunSync()

val http4sClient = new Http4sClient(
  emberClient,
  org.http4s.Uri.unsafeFromString("http://localhost:9200")
)

val client: ElasticClient[IO] = new ElasticClient[IO](http4sClient)
```

---

## ZIOHttpClient

```scala
import com.sksamuel.elastic4s.ziohttp.ZIOHttpClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticNodeEndpoint}
import zio.http.Client
import zio.{Task, ZIO}

val endpoint = ElasticNodeEndpoint("http", "localhost", 9200, None)

val clientLayer = Client.default >>> ZIO.service[Client].map { zioHttpClient =>
  ElasticClient(ZIOHttpClient(zioHttpClient, endpoint))
}
```

---

## Effect types

elastic4s uses `cats.Functor[F[_]]` internally. `ElasticClient[F]` returns `F[Response[U]]`.

### Scala Future (default)

```scala
import cats.implicits.catsStdInstancesForFuture
import scala.concurrent.ExecutionContext.Implicits.global

val client: ElasticClient[scala.concurrent.Future] =
  ElasticClient(JavaClient(ElasticProperties("http://localhost:9200")))

val future: Future[Response[SearchResponse]] = client.execute {
  search("index").query(matchAllQuery())
}
```

### ZIO Task

```scala
import com.sksamuel.elastic4s.zio.instances._   // brings Functor[zio.Task]
// or:
import zio.interop.catz._                        // via zio-interop-cats

val client: ElasticClient[zio.Task] = ...        // use ZIOHttpClient backend
```

### Monix Task

```scala
import com.sksamuel.elastic4s.monix.instances._  // brings Functor[monix.eval.Task]
```

### Scalaz Task

```scala
import com.sksamuel.elastic4s.scalaz.instances._ // brings Functor[scalaz.concurrent.Task]
```

---

## Handling the Response

`client.execute(...)` returns `F[Response[U]]`. `Response[U]` is a sealed trait:

```scala
sealed trait Response[+U]
case class RequestSuccess[U](status: Int, body: Option[String], headers: Map[String, String], result: U) extends Response[U]
case class RequestFailure(status: Int, body: Option[String], headers: Map[String, String], error: ElasticError) extends Response[Nothing]
```

Pattern matching:

```scala
client.execute { search("index").query(matchAllQuery()) }.foreach {
  case RequestSuccess(_, _, _, result) =>
    println(s"Got ${result.hits.total.value} hits")
  case RequestFailure(status, _, _, error) =>
    println(s"Failed ($status): ${error.reason}")
}
```

Direct access (throws on failure — useful in tests):

```scala
val result: SearchResponse = response.result
```

### ElasticError fields

```scala
error.`type`   // String: e.g. "index_not_found_exception"
error.reason   // String: human-readable message
error.rootCause // Seq[ElasticError]: underlying causes
```

---

## Handlers

A `Handler[Req, Resp]` is the typeclass that tells `client.execute` how to serialize a request to an `ElasticRequest` (HTTP method + endpoint + body) and how to deserialize the HTTP response into `Resp`.

**You do not import handlers individually in normal use.** `import com.sksamuel.elastic4s.ElasticDsl._` brings every handler into scope as implicit objects automatically — that single import is all you need.

```scala
import com.sksamuel.elastic4s.ElasticDsl._

// Handler[SearchRequest, SearchResponse] is in scope — no extra import
client.execute { search("my-index").query(matchAllQuery()) }

// Handler[IndexRequest, IndexResponse] is in scope — no extra import
client.execute { indexInto("my-index").id("1").doc(myObj) }
```

### Dump a request as JSON (`.show`)

`ElasticDsl._` also provides a `.show` extension method on every request type that serializes it to a readable HTTP request string:

```scala
import com.sksamuel.elastic4s.ElasticDsl._

val req = search("my-index")
  .query(boolQuery().must(matchQuery("name", "elastic")).filter(termQuery("status", "active")))
  .aggs(termsAgg("by_cat", "category"))

println(req.show)
// GET /my-index/_search
// {"query":{"bool":{"must":[{"match":{"name":{"query":"elastic"}}}],
//  "filter":[{"term":{"status":{"value":"active"}}}]}},
//  "aggs":{"by_cat":{"terms":{"field":"category"}}}}

// Or get the raw ElasticRequest object:
val elasticReq = req.request
elasticReq.method    // "GET"
elasticReq.endpoint  // "/my-index/_search"
elasticReq.params    // Map[String, String]
elasticReq.entity    // Option[HttpEntity] — the JSON body
```

### Handler typeclass definition

```scala
// com.sksamuel.elastic4s.Handler
abstract class Handler[T, U] {
  def build(t: T): ElasticRequest      // serialize request → HTTP
  def responseHandler: ResponseHandler[U]  // deserialize HTTP → U
}
```

### When individual handler imports are needed

In rare cases — e.g. you only mix in part of `ElasticDsl`, or you write a utility outside the DSL scope — you can import individual handler traits:

```scala
import com.sksamuel.elastic4s.requests.searches.SearchHandlers       // SearchRequest
import com.sksamuel.elastic4s.handlers.index.IndexHandlers            // IndexRequest
import com.sksamuel.elastic4s.handlers.delete.DeleteHandlers          // DeleteByIdRequest, DeleteByQueryRequest
import com.sksamuel.elastic4s.handlers.index.mapping.MappingHandlers  // PutMappingRequest, GetMappingRequest
import com.sksamuel.elastic4s.handlers.explain.ExplainHandlers        // ExplainRequest
import com.sksamuel.elastic4s.handlers.update.UpdateHandlers          // UpdateRequest
import com.sksamuel.elastic4s.handlers.bulk.BulkHandlers              // BulkRequest
import com.sksamuel.elastic4s.handlers.cat.CatHandlers                // catIndices, catShards…
import com.sksamuel.elastic4s.handlers.validate.ValidateHandlers      // ValidateRequest
import com.sksamuel.elastic4s.handlers.pit.PitHandlers                // createPointInTime, deletePointInTime
```

### Custom aggregation handler

If you define a custom aggregation type (implementing `AbstractAggregation`), use `ElasticDsl.withCustomAggregationHandler` to inject its serializer:

```scala
import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation

case class MyAgg(name: String, field: String) extends AbstractAggregation

val dsl = ElasticDsl.withCustomAggregationHandler {
  case agg: MyAgg =>
    val builder = XContentBuilder()
    builder.startObject("my_agg")
    builder.field("field", agg.field)
    builder.endObject()
    builder
}

// Use dsl instead of ElasticDsl._
import dsl._
client.execute { search("idx").aggs(MyAgg("my_result", "category")) }
```

---

## Client lifecycle

```scala
// Always close the client when done (releases underlying HTTP connections)
client.close()

// With Future:
import scala.concurrent.Await
import scala.concurrent.duration._
Await.result(client.close(), 10.seconds)
```
