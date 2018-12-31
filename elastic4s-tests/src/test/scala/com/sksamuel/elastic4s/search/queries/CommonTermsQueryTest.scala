package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.Preference
import com.sksamuel.elastic4s.{ElasticDsl, Indexable}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CommonTermsQueryTest extends WordSpec with Matchers with DockerTests with ElasticDsl {

  case class Condiment(name: String, desc: String)
  implicit object CondimentIndexable extends Indexable[Condiment] {
    override def json(t: Condiment): String = s""" { "name" : "${t.name}", "desc" : "${t.desc}" } """
  }

  val ketchup = Condiment("ketchup",
    "Ketchup or catsup, is a table sauce. Traditionally, different recipes featured ketchup made of mushrooms, oysters, mussels, walnuts, or other foods")
  val brownSauce = Condiment("BrownSauce",
    "Brown sauce is a traditional condiment served with food in the United Kingdom and Ireland, normally brown or dark orange in colour")
  // my american wife loves Ranch but I think it's bloody disgusting :)
  val ranch = Condiment("Ranch",
    "Ranch dressing is a type of salad dressing made of some combination of buttermilk, salt, garlic, onion, herbs")

  Try {
    client.execute {
      ElasticDsl.deleteIndex("condiments")
    }.await
  }

  client.execute {
    bulk(
      indexInto("condiments" / "test") source ranch,
      indexInto("condiments" / "test") source ketchup,
      indexInto("condiments" / "test") source brownSauce
    ).refreshImmediately
  }.await

  "common query" should {
    "perform query" in {
      val resp = client.execute {
        search("condiments") query {
          commonTermsQuery("desc") text "catsup"
        }
      }.await.result
      resp.totalHits shouldBe 1
    }
    "use operators" in {
      val resp = client.execute {
        search("condiments") query {
          commonTermsQuery("desc") text "buttermilk somethingnotindexed" lowFreqOperator "AND" highFreqOperator "AND"
        }
      }.await.result
      resp.totalHits shouldBe 0
    }
    "use lowFreqMinimumShouldMatch" in {
      val resp = client.execute {
        search("condiments") query {
          commonTermsQuery("desc") text "buttermilk dressing salt garlic" lowFreqMinimumShouldMatch 2
        }
      }.await.result
      resp.totalHits shouldBe 1
    }
    "use preference" in {
      val resp = client.execute {
        search("condiments") query {
          commonTermsQuery("desc") text "catsup"
        } preference Preference.Local
      }.await.result
      resp.totalHits shouldBe 1
    }
  }
}
