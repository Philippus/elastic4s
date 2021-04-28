package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.requests.script.Script
import org.json4s._
import org.json4s.jackson.Serialization
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class Json4sParamSerializerTest extends AnyWordSpec with Matchers {

  case class Foo(bar: Int)

  "A derived ParamSerializer instance from the elastic4s-json-json4s package" should {

    "be implicitly found and used for parameter serialization" in {

      import ElasticJson4s.Implicits._
      implicit val formats: Formats = Serialization.formats(NoTypeHints)
      implicit val serialization: Serialization = Serialization

      Script("some script")
        .paramObject("some param", Foo(77))
        .paramsRaw shouldBe Map("some param" -> """{"bar":77}""")
    }
  }
}
