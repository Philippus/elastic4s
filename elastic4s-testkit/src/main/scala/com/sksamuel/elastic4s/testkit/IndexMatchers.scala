package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.{ElasticClient, HitReader}
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.Future

trait IndexMatchers extends Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  import scala.concurrent.duration._

  def haveCount(expectedCount: Int)(implicit
      client: ElasticClient[Future],
      timeout: FiniteDuration = 10.seconds
  ): Matcher[String] =
    new Matcher[String] {

      def apply(left: String): MatchResult = {
        val count = client.execute(search(left).size(0)).await(timeout).result.totalHits
        MatchResult(
          count == expectedCount,
          s"Index $left had count $count but expected $expectedCount",
          s"Index $left had document count $expectedCount"
        )
      }
    }

  def haveDocument[T](expectedId: String, expectedDocument: T)(
      implicit
      client: ElasticClient[Future],
      hitReader: HitReader[T],
      timeout: FiniteDuration = 10.seconds
  ): Matcher[String] = {
    (left: String) =>
      {
        val resp                = client.execute(get(left, expectedId)).await(timeout).result
        val maybeActualDocument = resp.toOpt[T]
        val exists              = maybeActualDocument.contains(expectedDocument)
        MatchResult(
          exists,
          maybeActualDocument.fold(
            s"Index $left did not contain expected document $expectedId"
          )(actualDocument =>
            s"Index $left did not contain $expectedDocument but $actualDocument"
          ),
          s"Index $left contained unwanted $expectedDocument"
        )
      }
  }

  def containDoc(expectedId: Any)(implicit
      client: ElasticClient[Future],
      timeout: FiniteDuration = 10.seconds
  ): Matcher[String] =
    new Matcher[String] {

      override def apply(left: String): MatchResult = {
        val exists = client.execute(get(left, expectedId.toString)).await(timeout).result.exists
        MatchResult(
          exists,
          s"Index $left did not contain expected document $expectedId",
          s"Index $left contained document $expectedId"
        )
      }
    }

  def beCreated(implicit client: ElasticClient[Future], timeout: FiniteDuration = 10.seconds): Matcher[String] =
    new Matcher[String] {

      override def apply(left: String): MatchResult = {
        val exists = client.execute(indexExists(left)).await(timeout).result.isExists
        MatchResult(
          exists,
          s"Index $left did not exist",
          s"Index $left exists"
        )
      }
    }

  def beEmpty(implicit client: ElasticClient[Future], timeout: FiniteDuration = 10.seconds): Matcher[String] =
    new Matcher[String] {

      override def apply(left: String): MatchResult = {
        val count = client.execute(search(left).size(0)).await(timeout).result.totalHits
        MatchResult(
          count == 0,
          s"Index $left was not empty",
          s"Index $left was empty"
        )
      }
    }
}
