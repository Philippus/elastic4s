package com.sksamuel.elastic4s.streams.fs2

import cats.Applicative
import cats.syntax.functor._
import com.sksamuel.elastic4s.http.ElasticDsl.{clearScroll, searchScroll}
import com.sksamuel.elastic4s.http.search.{SearchHit, SearchResponse}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.searches.{SearchRequest, SearchScrollRequest}
import com.sksamuel.exts.Logging
import com.sksamuel.exts.OptionImplicits._
import fs2._
import scala.concurrent.TimeoutException
import scala.concurrent.duration._
import scala.language.higherKinds

class Fs2Subscriber[F[_]: Applicative: Executor: Functor: RaiseThrowable](client: ElasticClient)
    extends ElasticDsl {
  type ScrollId = String

  val defaultKeepAlive: FiniteDuration = 1.minute

  def stream(query: SearchRequest): Stream[F, SearchHit] = {
    val keepAlive = query.keepAlive.getOrElse(defaultKeepAlive.toSeconds + "s")

    def getNextResponse(response: Response[SearchResponse]) : Stream[F, SearchHit] = response match {
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
        val searchHits = response.result.hits.hits
        response.result.scrollId match {
          case Some(id) =>
            Stream.emits(searchHits).onFinalize[F] {
              client.execute(clearScroll(id)).map(_ => ())
            }
          case None =>
            Stream.emits(searchHits)
        }

      case response: RequestSuccess[SearchResponse] =>
        response.result.scrollId match {
          case Some(id) =>
            Stream.emits(response.result.hits.hits) ++
              Stream.eval(client.execute(searchScroll(id, keepAlive))).flatMap(getNextResponse)
          case None =>
            Stream.raiseError(new IllegalStateException("Response did not include a scroll id"))
        }
    }

    Stream
      .eval(client.execute(query.keepAlive(keepAlive)))
      .flatMap(getNextResponse)
  }

}
