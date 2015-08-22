package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{SearchDefinition, ElasticClient, QueryDefinition}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.duration._

trait SearchMatchers extends ElasticMatchers {

  def haveHits(expectedCount: Int)
              (implicit client: ElasticClient,
               timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val count = resp.hits.length
      MatchResult(
        count == expectedCount,
        s"Search $left found $count hits",
        s"Search $left found $count hits"
      )
    }
  }

  def haveNoHits(implicit client: ElasticClient,
                 timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val count = resp.hits.length
      MatchResult(
        count == 0,
        s"Search $left found $count hits",
        s"Search $left found $count hits"
      )
    }
  }
}
