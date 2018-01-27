package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.TcpClient
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

trait IndexMatchers extends Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  import scala.concurrent.duration._

  def haveCount(expectedCount: Int)(implicit client: TcpClient, timeout: FiniteDuration = 10.seconds): Matcher[String] =
    new Matcher[String] {

      def apply(left: String): MatchResult = {
        val count = client.execute(search(left).size(0)).await(timeout).totalHits
        MatchResult(
          count == expectedCount,
          s"Index $left had count $count but expected $expectedCount",
          s"Index $left had document count $expectedCount"
        )
      }
    }

  def containDoc(expectedId: Any)(implicit client: TcpClient, timeout: FiniteDuration = 10.seconds): Matcher[String] =
    new Matcher[String] {

      override def apply(left: String): MatchResult = {
        val exists = client.execute(get(expectedId.toString).from(left)).await(timeout).exists
        MatchResult(
          exists,
          s"Index $left did not contain expected document $expectedId",
          s"Index $left contained document $expectedId"
        )
      }
    }

  def beCreated(implicit client: TcpClient, timeout: FiniteDuration = 10.seconds): Matcher[String] =
    new Matcher[String] {

      override def apply(left: String): MatchResult = {
        val exists = client.execute(indexExists(left)).await(timeout).isExists
        MatchResult(
          exists,
          s"Index $left did not exist",
          s"Index $left exists"
        )
      }
    }

  def beEmpty(implicit client: TcpClient, timeout: FiniteDuration = 10.seconds): Matcher[String] = new Matcher[String] {

    override def apply(left: String): MatchResult = {
      val count = client.execute(search(left).size(0)).await(timeout).totalHits
      MatchResult(
        count == 0,
        s"Index $left was not empty",
        s"Index $left was empty"
      )
    }
  }
}
