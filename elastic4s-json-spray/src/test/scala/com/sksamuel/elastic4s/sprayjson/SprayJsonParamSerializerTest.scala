package com.sksamuel.elastic4s.sprayjson

import com.sksamuel.elastic4s.requests.script.Script
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import spray.json._

class SprayJsonParamSerializerTest extends AnyWordSpec with Matchers {

  "A derived ParamSerializer instance from the elastic4s-json-spray package" should {

    "be implicitly found and used for parameter serialization" in {

      case class Foo(bar: Int)

      object FooJsonProtocol extends DefaultJsonProtocol {
        implicit val fooJsonFormat: RootJsonFormat[Foo] = jsonFormat1(Foo)
      }

      import FooJsonProtocol._

      Script("some script")
        .paramObject("some param", Foo(77))
        .paramsRaw shouldBe Map("some param" -> """{"bar":77}""")
    }
  }
}
