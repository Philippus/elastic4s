package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl}
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

trait IndexMatchers extends Matchers {

  def haveDocCount(expectedCount: Int)(implicit client: ElasticClient): Matcher[String] = new Matcher[String] {
    def apply(left: String) = {
      import ElasticDsl._
      val count = client.execute(countFrom(left)).await.getCount
      MatchResult(
        count == expectedCount,
        s"Index $left had count $count but expected $expectedCount",
        s"Index $left had document count $expectedCount"
      )
    }
  }
}
