package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class SearchHitReaderTest extends FlatSpec with Matchers with DockerTests {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  "SearchHit" should "support HitReader[T] for complex types" in {

    val focus = Car(
      name = "Focus",
      manufacturer = Manufacturer("Ford", Seq(Location("Detroit"), Location("Slough"), Location("Mexico City"))),
      models = Map("GTI" -> Seq(Feature("Air Con"), Feature("Power Steering")), "Sport" -> Seq(Feature("Spoiler")))
    )

    client.execute {
      createIndex("cars").mappings(
        mapping("models")
      )
    }.await

    client.execute {
      indexInto("cars" / "models").doc(focus).refresh(RefreshPolicy.Immediate)
    }.await

    Thread.sleep(3000)

    client.execute {
      search("cars").matchAllQuery().limit(1)
    }.await.result.safeTo[Car] shouldBe Seq(Right(focus))
  }
}

case class Car(name: String, manufacturer: Manufacturer, models: Map[String, Seq[Feature]])
case class Manufacturer(name: String, locations: Seq[Location])
case class Location(name: String)
case class Feature(name: String)
