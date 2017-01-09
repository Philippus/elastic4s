//package com.sksamuel.elastic4s.search
//
//import com.sksamuel.elastic4s.Indexable
//import com.sksamuel.elastic4s.testkit.ElasticSugar
//import org.scalatest.{FlatSpec, Matchers}
//
//case class Element(name: String, discoverer: String)
//
//class SearchTemplateTest extends FlatSpec with Matchers with ElasticSugar {
//
//  implicit object ElementIndexable extends Indexable[Element] {
//    override def json(t: Element): String = {
//      s""" { "name" : "${t.name}}", "discoverer" : "${t.discoverer}" } """
//    }
//  }
//
//  client.execute {
//    createIndex("elements").mappings(
//      mapping("element").as(
//        keywordField("name"),
//        textField("discoverer")
//      )
//    )
//  }.await
//
//  client.execute {
//    bulk(
//      indexInto("elements" / "element").source(Element("Phosphorus", "Brand")),
//      indexInto("elements" / "element").source(Element("Cobalt", "Brandt")),
//      indexInto("elements" / "element").source(Element("Hydrogen", "Cavendish")),
//      indexInto("elements" / "element").source(Element("Potassium", "Humprey Davy")),
//      indexInto("elements" / "element").source(Element("Barium", "Scheele"))
//    )
//  }
//
//  blockUntilCount(5, "elements")
//
//  "search template" should "use params" in {
//    val resp = client.execute {
//      templateSearch(
//        search("elements" / "elements")
//      ).params(Map("myfield" -> "name", "myterm" -> "Brand"))
//    }.await.response
//
//    resp.totalHits shouldBe 1
//  }
//}
