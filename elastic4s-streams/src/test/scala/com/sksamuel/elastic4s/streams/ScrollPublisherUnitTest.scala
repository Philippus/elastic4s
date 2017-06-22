//package com.sksamuel.elastic4s.streams
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.ElasticDsl
//import com.sksamuel.elastic4s.testkit.ElasticSugar
//import org.scalatest.{WordSpec, Matchers}
//
//class ScrollPublisherUnitTest extends WordSpec with Matchers with ElasticSugar {
//
//  import ElasticDsl._
//  import ReactiveElastic._
//
//  implicit val system = ActorSystem()
//
//  "elastic-streams" should {
//    "throw exception if search definition has no scroll" in {
//      an [IllegalArgumentException] should be thrownBy
//        client.publisher(search in "scrollpubint" / "emperors" query "*:*")
//    }
//  }
//}
