package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import org.scalatest.{OneInstancePerTest, Matchers, WordSpec}

class CommonQueryTest extends WordSpec with Matchers with ElasticSugar with OneInstancePerTest {

  case class Condiment(name: String, desc: String)
  implicit object CondimentIndexable extends Indexable[Condiment] {
    override def json(t: Condiment): String = s""" { "name" : "${t.name}", "desc" : "${t.desc}" } """
  }

  val ketchup = Condiment("ketchup",
    "Ketchup (Listeni/ˈkɛtʃəp/ or Listeni/ˈkɛtʃʌp/), or catsup, is a table sauce. Traditionally, different recipes featured ketchup made of mushrooms, oysters, mussels, walnuts, or other foods, but in modern times the term without modification usually refers to tomato ketchup, often called tomato sauce, or, occasionally, red sauce. It is a sweet and tangy sauce, typically made from tomatoes, a sweetener, vinegar, and assorted seasonings and spices. Seasonings vary by recipe, but commonly include onions, allspice, cloves, cinnamon, garlic, and sometimes celery. Heinz tomato ketchup, which contains 23.7g sugar and 3.1g of salt per 100g, is the market leader, with an 82% market share in the UK.")
  val brownSauce = Condiment("BrownSauce",
    "Brown sauce is a traditional condiment served with food in the United Kingdom and Ireland, normally brown or dark orange in colour. The best known brown sauce is HP Sauce, a spicy and tangy variety. Brown sauce is traditionally eaten with meals and dishes such as full breakfasts, bacon sandwiches, chips, and baked beans.\nThe ingredients include a varying combination of tomatoes, molasses, dates, tamarind, spices, vinegar, and sometimes raisins or anchovies. The taste is either tart or sweet with a peppery taste similar to Worcestershire. It is similar but not identical to today's steak sauce in the United States, which historically derives from brown sauce; barbecue sauce in Australia; and tonkatsu sauce in Japan.")
  // my american wife loves Ranch but it's bloody disgusting.
  val ranch = Condiment("Ranch",
    "Ranch dressing is a type of salad dressing made of some combination of buttermilk, salt, garlic, onion, herbs (commonly chives, parsley, and dill), and spices (commonly black pepper, paprika, and ground mustard seed), mixed into a sauce. Sour cream and yogurt are sometimes used as a substitute by some home cooks or to create a lower-fat version. Ranch dressing has been the best-selling salad dressing in the United States since 1992, when it overtook Italian dressing. It is also popular as a dip.")

  client.execute {
    bulk(
      index into "condiments" / "test" source ranch,
      index into "condiments" / "test" source ketchup,
      index into "condiments" / "test" source brownSauce
    )
  }.await

  refresh("condiments")
  blockUntilCount(2, "condiments")

  "common query" should {
    "use lowFreqMinimumShouldMatch" in {
      val resp = client.execute {
        search in "condiments" / "test" query {
          commonQuery("desc") text "United" lowFreqMinimumShouldMatch 2
        }
      }.await
      resp.getHits
    }
  }
}
