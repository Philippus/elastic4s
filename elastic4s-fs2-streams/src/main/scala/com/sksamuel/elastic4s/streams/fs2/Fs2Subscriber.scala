package com.sksamuel.elastic4s.streams.fs2

import cats.Applicative
import cats.syntax.functor._
import com.sksamuel.elastic4s.http.ElasticDsl.{clearScroll, searchScroll}
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.searches.{SearchRequest, SearchScrollRequest}
import com.sksamuel.exts.Logging
import com.sksamuel.exts.OptionImplicits._
import fs2._
import scala.language.higherKinds
import scala.util.Success

class Fs2Subscriber[F[_]: Applicative: Executor: Functor: RaiseThrowable](client: ElasticClient)
    extends ElasticDsl {
  type ScrollId = String

  def stream(query: SearchRequest): Stream[F, Response[SearchResponse]] = {
    val keepAlive = query.keepAlive.getOrElse("1m")

//    def getNextResponse(response: Response[SearchResponse]): Stream[F, Response[SearchResponse]] =
//      response.result.scrollId match {
//        case Some(id) =>
//          Stream(response).evalMap[F, Response[SearchResponse]] { response =>
//            println(s"got the next scrollId: $id with results: ${response.result.hits.size}")
//            client.execute(searchScroll(id, keepAlive))
//          }
//        case None =>
//          Stream(response)
//      }
    def getNextResponse(response: Response[SearchResponse]) : Stream[F, Response[SearchResponse]] = response match {
      case response: RequestFailure =>
        logger.warn("Request errored, will terminate the subscription", response.error.toString)
        Stream.raiseError[F](new RuntimeException(response.error.toString))
//        context.stop(self)
      // handle when the es request times out
      case response: RequestSuccess[SearchResponse] if response.result.isTimedOut =>
        logger.warn("Elasticsearch request timed out; will terminate the subscription")
        Stream.raiseError[F](new RuntimeException("Request terminated early or timed out"))
//        context.stop(self)
      // if we had no results from ES then we have nothing left to publish and our work here is done
      case response: RequestSuccess[SearchResponse] if response.result.isEmpty =>
        logger.debug("Response from ES came back empty; this means no more items upstream so will complete subscription")
//        s.onComplete()
//        client.execute(clearScroll(scrollId))
        response.result.scrollId match {
          case Some(id) =>
            Stream[F, Response[SearchResponse]](response).onFinalize[F] {
              client.execute(clearScroll(id)).map(_ => ())
            }

          case None =>
            Stream(response)
        }
//        context.stop(self)
      // more results and we can unleash the beast (stashed requests) and switch back to ready mode
      case response: RequestSuccess[SearchResponse] =>
        response.result.scrollId match {
          case Some(id) =>
            Stream(response).flatMap[F, Response[SearchResponse]] { response =>
              println(s"got the next scrollId: $id with results: ${response.result.hits.size}")
              Stream.eval(client.execute(searchScroll(id, keepAlive))).flatMap(getNextResponse)
            }
          case None =>
            Stream.raiseError(new IllegalStateException("Response did not include a scroll id"))
        }
    }

    Stream
      .eval(client.execute(query))
      .flatMap(getNextResponse)
  }

}
