package com.sksamuel.elastic4s.circe

import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

case class Place(id: Int, name: String)
case class Cafe(name: String, place: Place)

class CodecDerivationTest extends AnyWordSpec with Matchers with GivenWhenThen {

  "A derived HitReader instance" should {

    "be implicitly found if circe.generic.auto is in imported" in {
      """
        import io.circe.generic.auto._
        import com.sksamuel.elastic4s.HitReader
        implicitly[HitReader[Cafe]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.HitReader
        implicitly[HitReader[Cafe]]
      """ shouldNot compile
    }
  }

  "A derived AggReader instance" should {
    "be implicitly found if circe.generic.auto is in imported" in {
      """
        import io.circe.generic.auto._
        import com.sksamuel.elastic4s.AggReader
        implicitly[AggReader[Cafe]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.AggReader
        implicitly[AggReader[Cafe]]
      """ shouldNot compile
    }
  }

  "A derived Indexable instance" should {
    "be implicitly found if circe.generic.auto is in imported" in {
      """
        import io.circe.generic.auto._
        import com.sksamuel.elastic4s.Indexable
        implicitly[Indexable[Cafe]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.Indexable
        implicitly[Indexable[Cafe]]
      """ shouldNot compile
    }
  }

  "A derived ParamSerializer instance" should {
    "be implicitly found if circe.generic.auto is in imported" in {
      """
        import io.circe.generic.auto._
        import com.sksamuel.elastic4s.ParamSerializer
        implicitly[ParamSerializer[Cafe]]
      """ should compile
    }

    "not compile if no decoder is in scope" in {
      """
        import com.sksamuel.elastic4s.ParamSerializer
        implicitly[ParamSerializer[Cafe]]
      """ shouldNot compile
    }
  }

}
