package com.sksamuel.elastic4s.streams.fs2

import cats.Applicative
import cats.syntax.functor._
import com.sksamuel.elastic4s.http.search.{SearchHit, SearchResponse}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.searches.SearchRequest
import com.sksamuel.exts.Logging
import fs2._
import scala.concurrent.TimeoutException
import scala.concurrent.duration._
import scala.language.higherKinds

/**
  * Search result subscriber which returns a [[fs2.Stream]] of [[SearchHit]]
  *
  * @param client The elastic client which is used under the hood.
  * @tparam F The stream effect type
  */
class SearchResultSubscriber[F[_]: Applicative: Functor: Executor: RaiseThrowable](client: ElasticClient)
    extends ElasticDsl with Logging {

  type ScrollId = String

  val defaultKeepAlive: FiniteDuration = 1.minute

  def stream(query: SearchRequest): Stream[F, SearchHit] = {
    val keepAlive = query.keepAlive.getOrElse(defaultKeepAlive.toSeconds + "s")

    def getNextResponse(response: Response[SearchResponse]): Stream[F, SearchHit] = response match {
      case response: RequestFailure =>
        val errorMessage = response.error.toString
        logger.warn("Request errored, will terminate the subscription", errorMessage)
        Stream.raiseError[F](new RuntimeException(errorMessage))

      // handle when the es request times out
      case response: RequestSuccess[SearchResponse] if response.result.isTimedOut =>
        logger.warn("Elasticsearch request timed out; will terminate the subscription")
        Stream.raiseError[F](new TimeoutException("Request terminated early or timed out"))

      // if we had no results from ES then we have nothing left to publish and our work here is done
      case response: RequestSuccess[SearchResponse] if response.result.isEmpty =>
        logger.debug("Response from ES came back empty; this means no more items upstream so will complete subscription")
        response.result.scrollId.foldLeft(
          Stream.emits[F, SearchHit](response.result.hits.hits)
        ) {
          case (stream, id) =>
            stream.onFinalize[F] {
              client.execute(clearScroll(id)).map(_ => ())
            }
        }

      case response: RequestSuccess[SearchResponse] =>
        response.result.scrollId match {
          case Some(id) =>
            Stream.emits(response.result.hits.hits) ++
              Stream
                .eval(client.execute(searchScroll(id, keepAlive)))
                .flatMap(getNextResponse)
          case None =>
            Stream.raiseError(new IllegalStateException("Response did not include a scroll id"))
        }
    }

    Stream
      .eval(client.execute(query.keepAlive(keepAlive)))
      .flatMap(getNextResponse)
  }

}
