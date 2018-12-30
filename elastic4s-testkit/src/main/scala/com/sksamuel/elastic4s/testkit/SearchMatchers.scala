package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.requests.searches.SearchRequest
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.duration._
import scala.language.higherKinds

trait SearchMatchers extends Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  def containId(expectedId: Any)(implicit client: ElasticClient,
                                 timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp   = client.execute(left).await(timeout).result
        val exists = resp.hits.hits.exists(_.id == expectedId.toString)
        MatchResult(
          exists,
          s"Search $left did not find document $expectedId",
          s"Search $left found document $expectedId"
        )
      }
    }

  def haveFieldValue(value: String)(implicit client: ElasticClient,
                                    timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp   = client.execute(left).await(timeout).result
        val exists = resp.hits.hits.flatMap(_.fields).toMap.contains(value)
        MatchResult(
          exists,
          s"Search ${left.indexesTypes.indexes.mkString(",")}/${left.indexesTypes.types.mkString(",")} did not contain field value '$value'",
          s"Search ${left.indexesTypes.indexes.mkString(",")}/${left.indexesTypes.types.mkString(",")} contained unwanted field value $value"
        )
      }
    }

  def haveSourceField(value: String)(implicit client: ElasticClient,
                                     timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp   = client.execute(left).await(timeout).result
        val exists = resp.hits.hits.exists(_.sourceAsMap.contains(value))
        MatchResult(
          exists,
          s"Search $left contained unwanted source field $value",
          s"Search $left did not contain unwanted source field $value"
        )
      }
    }

  def haveSourceFieldValue(field: String, value: String)(
    implicit client: ElasticClient,
    timeout: FiniteDuration = 10.seconds
  ): Matcher[SearchRequest] = new Matcher[SearchRequest] {
    override def apply(left: SearchRequest): MatchResult = {
      val resp   = client.execute(left).await(timeout).result
      val exists = resp.hits.hits.flatMap(_.sourceAsMap.get(field)).contains(value)
      MatchResult(
        exists,
        s"Search $left contained unwanted source field value $value",
        s"Search $left did not contain unwanted source field value $value"
      )
    }
  }

  def haveTotalHits(expectedCount: Int)(implicit client: ElasticClient,
                                        timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp  = client.execute(left).await(timeout).result
        val count = resp.totalHits
        MatchResult(
          count == expectedCount,
          s"Search $left found $count totalHits",
          s"Search $left found $count totalHits"
        )
      }
    }

  def haveHits(expectedCount: Int)(implicit client: ElasticClient,
                                   timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp  = client.execute(left).await(timeout).result
        val count = resp.hits.hits.length
        MatchResult(
          count == expectedCount,
          s"Search $left found $count hits",
          s"Search $left found $count hits"
        )
      }
    }

  def haveNoHits(implicit client: ElasticClient, timeout: FiniteDuration = 10.seconds): Matcher[SearchRequest] =
    new Matcher[SearchRequest] {
      override def apply(left: SearchRequest): MatchResult = {
        val resp  = client.execute(left).await(timeout).result
        val count = resp.hits.hits.length
        MatchResult(
          count == 0,
          s"Search $left found $count hits",
          s"Search $left found $count hits"
        )
      }
    }
}
