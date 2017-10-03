package com.sksamuel.elastic4s.circe

import com.sksamuel.elastic4s.searches.RichSearchHit
import org.elasticsearch.search.SearchHit
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class CodecDerivationTest extends WordSpec with Matchers with GivenWhenThen {

  case class Place(id: Int, name: String)

  case class Cafe(name: String, place: Place)

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

    "extract the correct values" in {
      Given("a search hit")
      val javaHit = MockitoSugar.mock[SearchHit]
      val hit = RichSearchHit(javaHit)

      val source =
        """
        { "name": "Cafe Blue", "place": { "id": 3, "name": "Munich" } }
      """
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

}
