package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class ChildrenAggregationHttpTest extends FlatSpec with Matchers with DockerTests {

  "child aggs" should "support sub aggs" ignore {

    Try {
      client.execute {
        deleteIndex("childrenaggs")
      }.await
    }

    client.execute {
      createIndex("childrenaggs") mappings(
        mapping("question") fields(
          textField("question").fielddata(true),
          dateField("date").format("dd/MM/yyyy")
        ),
        mapping("answer").fields(
          textField("text").fielddata(true)
        ).parent("question")
      )
    }.await

    client.execute {
      bulk(
        indexInto("childrenaggs/question").doc("""{ "question":"in quantum leap, why is sam leaping", "date":"10/09/2017" }""").id("1"),
        indexInto("childrenaggs/question").doc("""{ "question":"who is the best star trek character", "date":"28/11/2017" }""").id("2"),
        indexInto("childrenaggs/question").doc("""{ "question":"will GRRRRMartin ever finish book 6?", "date":"05/12/2017" }""").id("3"),
        indexInto("childrenaggs/answer").doc("""{ "text":"in the last episode he meets God" }""").parent("1"),
        indexInto("childrenaggs/answer").doc("""{ "text":"God indicates he's doing the leaping himself" }""").parent("1"),
        indexInto("childrenaggs/answer").doc("""{ "text":"It's clear that God is involved somehow" }""").parent("1"),
        indexInto("childrenaggs/answer").doc("""{ "text":"Obviously it's commander riker" }""").parent("2"),
        indexInto("childrenaggs/answer").doc("""{ "text":"Some would say Worf is better than even Riker" }""").parent("2"),
        indexInto("childrenaggs/answer").doc("""{ "text":"commander riker sits on chairs like a boss" }""").parent("2")
      ).refreshImmediately
    }.await

    // find the questions per month, then for each question link to the children, then find the top term in those children
    // so we're finding the top term per month
    val resp = client.execute {
      search("childrenaggs").matchAllQuery().aggs(
        dateHistogramAgg("agg1", "date").interval(DateHistogramInterval.Month).addSubagg(
          childrenAggregation("agg2", "answer").addSubagg(
            termsAgg("agg3", "text").size(1)
          )
        )
      )
    }.await.result

    val september = resp.aggs.dateHistogram("agg1").buckets.find(_.date == "01/09/2017").get
    val sept_answers = september.children("agg2")
    sept_answers.docCount shouldBe 3
    sept_answers.terms("agg3").buckets.head.key shouldBe "god"

    val november = resp.aggs.dateHistogram("agg1").buckets.find(_.date == "01/11/2017").get
    val nov_answers = november.children("agg2")
    nov_answers.docCount shouldBe 3
    nov_answers.terms("agg3").buckets.head.key shouldBe "riker"
  }
}
