package com.sksamuel.elastic4s.pekko.streams

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import com.sksamuel.elastic4s.ElasticDsl.search
import com.sksamuel.elastic4s.requests.common.Shards
import com.sksamuel.elastic4s.requests.searches._
import com.sksamuel.elastic4s.{
  CommonRequestOptions,
  ElasticClient,
  ElasticRequest,
  Handler,
  HttpClient,
  HttpResponse,
  RequestSuccess,
  Response
}
import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}
import java.util.concurrent.{ConcurrentLinkedQueue, CountDownLatch, TimeUnit}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.jdk.CollectionConverters._

class ElasticSourceTest extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  private implicit val system: ActorSystem                        = ActorSystem(getClass.getSimpleName)
  private implicit val ec: ExecutionContext                       = system.dispatcher
  private implicit val commonRequestOptions: CommonRequestOptions = CommonRequestOptions.defaults

  override protected def afterAll(): Unit =
    Await.result(system.terminate(), 10.seconds)

  "ElasticSource" should {
    "not complete while buffered hits remain after an empty prefetched scroll page" in {
      val page1     = (1 to 5).map(i => hit(i.toString)).toArray
      val responses = Vector(
        RequestSuccess(200, None, Map.empty, response("scroll-1", page1)),
        RequestSuccess(200, None, Map.empty, response("scroll-1", Array.empty))
      )

      val client   = scriptedClient(responses)
      val settings = SourceSettings(
        search("test-index").scroll("1m").size(5),
        maxItems = Long.MaxValue,
        fetchThreshold = 4,
        warm = false
      )

      val publisher = Source
        .fromGraph(new ElasticSource(client, settings))
        .runWith(Sink.asPublisher(fanout = false))

      val sub = new ManualSubscriber[SearchHit]
      publisher.subscribe(sub)

      sub.awaitSubscribed(1000) shouldBe true

      sub.request(1)
      sub.awaitElements(1, 2000) shouldBe true

      // Old behavior completes early here; fixed behavior must not complete yet.
      sub.completedWithin(300) shouldBe false

      sub.request(10)
      sub.completedWithin(3000) shouldBe true
      sub.error shouldBe null
      sub.items.map(_.id) should contain theSameElementsInOrderAs page1.map(_.id).toSeq
    }
  }

  private def scriptedClient(
      pages: Vector[RequestSuccess[SearchResponse]]
  ): ElasticClient[Future] = {
    val index = new AtomicInteger(0)

    new ElasticClient[Future](new HttpClient[Future] {
      override def send(request: ElasticRequest): Future[HttpResponse] =
        Future.failed(new UnsupportedOperationException("Not used in this test"))
      override def close(): Future[Unit]                               = Future.successful(())
    }) {
      override def execute[T, U](t: T)(implicit
          handler: Handler[T, U],
          options: CommonRequestOptions
      ): Future[Response[U]] =
        t match {
          case _: SearchRequest | _: SearchScrollRequest =>
            val i    = index.getAndIncrement()
            val next =
              if (i < pages.length) pages(i)
              else RequestSuccess(200, None, Map.empty, response("scroll-1", Array.empty))
            Future.successful(next.asInstanceOf[Response[U]])
          case _: ClearScrollRequest                     =>
            Future.successful(
              RequestSuccess(200, None, Map.empty, ClearScrollResponse(succeeded = true, num_freed = 1).asInstanceOf[U])
            )
          case other                                     =>
            Future.failed(new IllegalArgumentException(s"Unexpected request type: ${other.getClass.getName}"))
        }
    }
  }

  private def response(scrollId: String, hits: Array[SearchHit]): SearchResponse =
    SearchResponse(
      took = 1,
      isTimedOut = false,
      isTerminatedEarly = false,
      suggest = Map.empty,
      _shards = Shards(1, 1, 0),
      scrollId = Some(scrollId),
      pitId = None,
      _aggregationsAsMap = Map.empty,
      hits = SearchHits(Total(hits.length.toLong, "eq"), maxScore = 1.0, hits = hits)
    )

  private def hit(id: String): SearchHit =
    SearchHit(
      id = id,
      index = "test-index",
      version = 1L,
      seqNo = 0L,
      primaryTerm = 1L,
      score = 1.0F,
      parent = None,
      shard = None,
      node = None,
      routing = None,
      explanation = None,
      sort = None,
      _source = Map("id" -> id),
      fields = Map.empty,
      _highlight = None,
      inner_hits = Map.empty,
      matchedQueries = None
    )
}

private final class ManualSubscriber[T] extends Subscriber[T] {
  private val subscribed                  = new CountDownLatch(1)
  private val done                        = new CountDownLatch(1)
  private val err                         = new AtomicReference[Throwable](null)
  private val q                           = new ConcurrentLinkedQueue[T]()
  @volatile private var sub: Subscription = _

  override def onSubscribe(s: Subscription): Unit = {
    sub = s
    subscribed.countDown()
  }

  override def onNext(t: T): Unit = q.add(t)

  override def onError(t: Throwable): Unit = {
    err.set(t)
    done.countDown()
  }

  override def onComplete(): Unit = done.countDown()

  def awaitSubscribed(timeoutMs: Long): Boolean =
    subscribed.await(timeoutMs, TimeUnit.MILLISECONDS)

  def request(n: Long): Unit = sub.request(n)

  def items: Vector[T] = q.iterator().asScala.toVector

  def awaitElements(atLeast: Int, timeoutMs: Long): Boolean = {
    val deadline = System.nanoTime() + timeoutMs * 1000000L
    while (items.size < atLeast && System.nanoTime() < deadline) Thread.sleep(10)
    items.size >= atLeast
  }

  def completedWithin(timeoutMs: Long): Boolean =
    done.await(timeoutMs, TimeUnit.MILLISECONDS)

  def error: Throwable = err.get()
}
