package com.sksamuel.elastic4s.ziojson

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ZIOJsonParamSerializerTest extends AnyWordSpec with Matchers {

  case class Place(id: Int, name: String)

  "A derived HitReader instance" should {

    "be implicitly derived if a JsonDecoder is in scope" in {
      """
        import zio.json._
        import com.sksamuel.elastic4s.HitReader

        implicit val decoder: JsonDecoder[Place] = DeriveJsonDecoder.gen[Place]

        implicitly[HitReader[Place]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.HitReader
        implicitly[HitReader[Place]]
      """ shouldNot compile
    }
  }

  "A derived AggReader instance" should {
    "be implicitly derived if a JsonDecoder is in scope" in {
      """
        import zio.json._
        import com.sksamuel.elastic4s.AggReader

        implicit val decoder: JsonDecoder[Place] = DeriveJsonDecoder.gen[Place]

        implicitly[AggReader[Place]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.AggReader
        implicitly[AggReader[Place]]
      """ shouldNot compile
    }
  }

  "A derived Indexable instance" should {
    "be implicitly derived if a JsonEncoder is in scope" in {
      """
        import zio.json._
        import com.sksamuel.elastic4s.Indexable

        implicit val encoder: JsonEncoder[Place] = DeriveJsonEncoder.gen[Place]

        implicitly[Indexable[Place]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.Indexable
        implicitly[Indexable[Place]]
      """ shouldNot compile
    }
  }

  "A derived ParamSerializer instance" should {
    "be implicitly derived if a JsonEncoder is in scope" in {
      """
        import zio.json._
        import com.sksamuel.elastic4s.ParamSerializer

        implicit val encoder: JsonEncoder[Place] = DeriveJsonEncoder.gen[Place]

        implicitly[ParamSerializer[Place]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.ParamSerializer
        implicitly[ParamSerializer[Place]]
      """ shouldNot compile
    }
  }
}
