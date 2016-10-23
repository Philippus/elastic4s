package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.search.SearchDefinition

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * Represents something which can iterate the results from a query.
 *
 * In practice this means concatenating several requests into a single iterable, so that the next request is only
 * made when required when the iterable is advanced.
 *
 * @define ONDEMAND_ITERATOR an iterator which makes requests on-demand as it is advanced
 * @define QUERY the search queries over which to iterate
 */
trait IterableSearch {

  /**
   * This is the only abstract method for this trait.
   *
   * @param query $QUERY
   * @return $ONDEMAND_ITERATOR
   */
  def iterateSearch(query: SearchDefinition)(implicit timeout: Duration): Iterator[RichSearchResponse]

  /**
   *
   * @param queries $QUERY
   * @return $ONDEMAND_ITERATOR
   */
  def iterateSearch(queries: Iterable[SearchDefinition])(implicit timeout: Duration): Iterator[RichSearchResponse] = {
    queries.iterator.flatMap(iterateSearch)
  }

  /**
   * Support for a var-args type invocation
   *
   * @param firstQuery $QUERY
   * @param secondQuery $QUERY
   * @param theRest $QUERY
   * @return $ONDEMAND_ITERATOR
   */
  def iterateSearch(
                     firstQuery: SearchDefinition,
                     secondQuery: SearchDefinition,
                     theRest: SearchDefinition*)(implicit timeout: Duration): Iterator[RichSearchResponse] = {
    iterateSearch(firstQuery +: secondQuery +: theRest)
  }

  /** returns an $ONDEMAND_ITERATOR
    * @param query $QUERY
    * @return $ONDEMAND_ITERATOR
    */
  def iterate(query: SearchDefinition)(implicit timeout: Duration): Iterator[RichSearchHit] = {
    iterate(query :: Nil)
  }

  /**
   * @param queries $QUERY
   * @return $ONDEMAND_ITERATOR
   */
  def iterate(queries: Iterable[SearchDefinition])(implicit timeout: Duration): Iterator[RichSearchHit] = {
    iterateSearch(queries).flatMap(_.hits)
  }

}

object IterableSearch {

  private class ElasticIterable(client: ElasticClient, keepAlive: String) extends IterableSearch {

    override def iterateSearch(query: SearchDefinition)(implicit timeout: Duration): Iterator[RichSearchResponse] = {
      iterateNext(query, keepAlive, None)
    }

    private def iterateNext(searchDef: SearchDefinition, keepAlive: String, scrollId: Option[String])
                           (implicit timeout: Duration): Iterator[RichSearchResponse] = {
      def next(future: Future[RichSearchResponse]): Iterator[RichSearchResponse] = {
        val response = Await.result(future, timeout)
        val hits = response.hits

        if (hits.isEmpty || response.scrollIdOpt.isEmpty) {
          Iterator.empty
        } else {
          Iterator(response) ++ iterateNext(searchDef, keepAlive, response.scrollIdOpt)
        }
      }

      import ElasticDsl._

      // we're either advancing a scroll id or issuing the first query w/ the keep alive set
      val future: Future[RichSearchResponse] = scrollId.fold(client.execute(searchDef.scroll(keepAlive))) { scrollId =>
        client.execute(search scroll scrollId keepAlive keepAlive)
      }

      next(future)
    }
  }

  /**
   * Creates an IterableSearch using the elastic client and the optional keep alive (which is used for the cursor)
   *
   *
   * @param client the elastic client used to make the queries
   * @param keepAlive the keep alive used for the scrollId (e.g. "5m")
   * @return an IterableSearch instance
   */
  def apply(client: ElasticClient, keepAlive: String = "5m"): IterableSearch = {
    new ElasticIterable(client, keepAlive)
  }

}
