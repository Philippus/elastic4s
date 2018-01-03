package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.HitReader
import com.sksamuel.elastic4s.http.{AsyncExecutor, Awaitable, HttpClient}
import com.sksamuel.elastic4s.searches.SearchDefinition

import scala.concurrent.duration.Duration

/**
  * A SearchIterator is used to create standard library iterator's from a search request.
  * The iterator will use a search scroll internally for lazy loading of the data.
  *
  * Each time the iterator needs to request more data, the iterator will block until the request
  * returns. If you require a completely lazy style iterator, consider using reactive streams.
  */
object SearchIterator {

  /**
    * Creates a new Iterator for instances of SearchHit by wrapping the given HTTP client.
    */
  def hits[F[_]: AsyncExecutor: Awaitable](
      client: HttpClient,
      searchdef: SearchDefinition)(implicit timeout: Duration): Iterator[SearchHit] = new Iterator[SearchHit] {
    require(searchdef.keepAlive.isDefined, "Search request must define keep alive value")

    import com.sksamuel.elastic4s.http.ElasticDsl._

    private var iterator: Iterator[SearchHit] = Iterator.empty
    private var scrollId: Option[String]      = None

    override def hasNext: Boolean = iterator.hasNext || {
      iterator = fetchNext()
      iterator.hasNext
    }

    override def next(): SearchHit = iterator.next()

    def fetchNext(): Iterator[SearchHit] = {

      // we're either advancing a scroll id or issuing the first query w/ the keep alive set
      val resp = scrollId match {
        case Some(id) => client.execute(searchScroll(id, searchdef.keepAlive.get))
        case None => client.execute(searchdef)
      }

      // in a search scroll we must always use the last returned scrollId
      val response = Awaitable[F].await(resp, timeout) match {
        case Right(success) => success.result
        case Left(failure)  => sys.error(failure.toString)
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
  def iterate[F[_]: AsyncExecutor: Awaitable, T](
      client: HttpClient,
      search: SearchDefinition)(implicit reader: HitReader[T], timeout: Duration): Iterator[T] =
    hits[F](client, search).map(_.to[T])
}
