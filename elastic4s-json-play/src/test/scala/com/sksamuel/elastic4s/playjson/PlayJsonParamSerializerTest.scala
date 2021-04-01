package com.sksamuel.elastic4s.playjson

import com.sksamuel.elastic4s.requests.script.Script
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Json, Writes}

class PlayJsonParamSerializerTest extends AnyWordSpec with Matchers {

  case class Foo(bar: Int)

  "A derived ParamSerializer instance from the elastic4s-json-play package" should {

    "be implicitly found and used for parameter serialization" in {

      implicit val fooWrites: Writes[Foo] = Json.writes[Foo]

      Script("some script")
        .paramObject("some param", Foo(77))
        .paramsRaw shouldBe Map("some param" -> """{"bar":77}""")
    }
  }
}
