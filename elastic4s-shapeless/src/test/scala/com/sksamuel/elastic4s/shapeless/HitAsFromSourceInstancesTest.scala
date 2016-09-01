package com.sksamuel.elastic4s.shapeless

import com.sksamuel.elastic4s.{RichSearchHit, HitAs}

import org.scalatest.{WordSpec, ShouldMatchers, GivenWhenThen}
import org.scalatest.mock.MockitoSugar._
import org.mockito.Mockito
import org.elasticsearch.search.SearchHit

import scala.collection.JavaConversions._

import HitAsFromSourceInstances._

class HitAsFromSourceInstancesTest extends WordSpec with ShouldMatchers with GivenWhenThen {
  
  "A derived HitAs instances from a flat case class" should {
    case class Place(id: Int, name: String)
    
    "be implicitly found" in {
      "implicitly[HitAs[Place]]" should compile
    }
    
    "not compile for nested case classes" in {
      case class Bar(name: String, place: Place)
      "implicitly[HitAs[Bar]]" shouldNot compile
    }
    
    "extract the correct values" in {
      Given("a search hit")
      val javaHit = mock[SearchHit]
      val hit = RichSearchHit(javaHit)
      
      val source: java.util.Map[String, Object] = Map(
          "id" -> new java.lang.Integer(3),
          "name" -> "Munich"         
      )
      
      When("it is parsed with HitAs instances")
      Mockito.when(javaHit.sourceAsMap).thenReturn(source)
      val places = hit.as[Place]
      
      Then("it contains the correct values")
      places.id should be(3)
      places.name should be("Munich")
      
    }
  }
  
}