package com.sksamuel.elastic4s.circe

import com.sksamuel.elastic4s.{ RichSearchHit, HitAs }

import org.scalatest.{ WordSpec, ShouldMatchers, GivenWhenThen }
import org.scalatest.mock.MockitoSugar._
import org.mockito.Mockito
import org.elasticsearch.search.SearchHit

import scala.collection.JavaConversions._

class CodecDerivationTest extends WordSpec with ShouldMatchers with GivenWhenThen {

  "A derived HitAs instances from a flat case class" should {
    case class Place(id: Int, name: String)

    "be implicitly found if circe.generic.auto is in imported" in {
      import io.circe.generic.auto._
      "implicitly[HitAs[Place]]" should compile
    }
    
    "not compile if no decoder is in scope" in {
      "implicitly[HitAs[Place]]" shouldNot compile
    }
    
    "extract the correct values" in {
      import io.circe.generic.auto._
      Given("a search hit")
      val javaHit = mock[SearchHit]
      val hit = RichSearchHit(javaHit)
      
      val source = """
        { "id": 3, "name": "Munich" }
      """
      
      When("it is parsed with HitAs instances")
      Mockito.when(javaHit.sourceAsString).thenReturn(source)
      val places = hit.as[Place]
      
      Then("it contains the correct values")
      places.id should be(3)
      places.name should be("Munich")
      
    }
  }

}