package com.sksamuel.elastic4s.json4s

import com.sksamuel.elastic4s.{Hit, HitReader}
import com.sksamuel.elastic4s.requests.script.Script
import org.json4s._
import org.json4s.jackson.Serialization
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

case class Doc(_id: String, name: String)

class Json4sParamSerializerTest extends AnyWordSpec with Matchers {

  case class Foo(bar: Int)

  "A derived ParamSerializer instance from the elastic4s-json-json4s package" should {

    "be implicitly found and used for parameter serialization" in {

      import ElasticJson4s.Implicits._
      implicit val formats: Formats             = Serialization.formats(NoTypeHints)
      implicit val serialization: Serialization = Serialization

      Script("some script")
        .paramObject("some param", Foo(77))
        .paramsRaw shouldBe Map("some param" -> """{"bar":77}""")
    }
  }

  "A derived HitReader instance" should {

    "read _id from hit metadata when missing in source" in {
      import ElasticJson4s.Implicits._
      implicit val formats: Formats             = Serialization.formats(NoTypeHints)
      implicit val serialization: Serialization = Serialization

      val hit = new Hit {
        override def id: String                       = "doc-1"
        override def index: String                    = "places"
        override def version: Long                    = 1L
        override def seqNo: Long                      = 0L
        override def primaryTerm: Long                = 1L
        override def sort: Option[Seq[AnyRef]]        = None
        override def sourceAsString: String           = """{"name":"Berlin"}"""
        override def sourceAsMap: Map[String, AnyRef] = Map("name" -> "Berlin")
        override def exists: Boolean                  = true
        override def score: Float                     = 1.0F
      }

      implicitly[HitReader[Doc]].read(hit).get shouldBe Doc("doc-1", "Berlin")
    }
  }
}
