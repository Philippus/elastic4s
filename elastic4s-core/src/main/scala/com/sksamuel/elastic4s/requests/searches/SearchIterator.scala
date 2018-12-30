package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.{ElasticClient, HitReader, RequestFailure, RequestSuccess}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.higherKinds

/**
  * A SearchIterator is used to create standard library iterator's from a search request.
  * The iterator will use a search scroll internally for lazy loading of the data.
  *
  * Each time the iterator needs to request more data, the iterator will block until the request
  * returns. If you require a completely lazy style iterator, consider using reactive streams.
  */
trait Awaitable[F[_]] {
  def result[U](f: F[U], timeout: Duration): U
}

object SearchIterator {

  /**
    * Creates a new Iterator for instances of SearchHit by wrapping the given HTTP client.
    */
  def hits(client: ElasticClient, searchreq: SearchRequest)(implicit timeout: Duration): Iterator[SearchHit] =
    new Iterator[SearchHit] {
      require(searchreq.keepAlive.isDefined, "Search request must define keep alive value")

      import com.sksamuel.elastic4s.ElasticDsl._

      private var iterator: Iterator[SearchHit] = Iterator.empty
      private var scrollId: Option[String]      = None

      override def hasNext: Boolean = iterator.hasNext || {
        iterator = fetchNext()
        iterator.hasNext
      }

      override def next(): SearchHit = iterator.next()

      def fetchNext(): Iterator[SearchHit] = {

        // we're either advancing a scroll id or issuing the first query w/ the keep alive set
        val f = scrollId match {
          case Some(id) => client.execute(searchScroll(id, searchreq.keepAlive.get))
          case None     => client.execute(searchreq)
        }

        val resp = Await.result(f, timeout)

        // in a search scroll we must always use the last returned scrollId
        val response = resp match {
          case RequestSuccess(_, _, _, result) => result
          case failure: RequestFailure         => sys.error(failure.toString)
        }

        scrollId = response.scrollId
        response.hits.hits.iterator
      }
    }

  /**
    * Creates a new Iterator for type T by wrapping the given HTTP client.
    * A typeclass HitReader[T] must be provided for marshalling of the search
    * responses into instances of type T.
    */
  def iterate[T](client: ElasticClient, searchreq: SearchRequest)(implicit
                                                                  reader: HitReader[T],
                                                                  timeout: Duration): Iterator[T] =
    hits(client, searchreq).map(_.to[T])
}
