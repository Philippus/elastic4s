package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/** @author Stephen Samuel */
class HelpersTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    index into "music/bands" fields (
      "name" -> "coldplay",
      "singer" -> "chris martin",
      "drummer" -> "will champion",
      "guitar" -> "johnny buckland"
    )
  }

  client.execute {
    index into "music/artists" fields (
      "name" -> "kate bush",
      "singer" -> "kate bush"
    )
  }

  client.execute {
    index into "music/bands" fields (
      "name" -> "jethro tull",
      "singer" -> "ian anderson",
      "guitar" -> "martin barre",
      "keyboards" -> "johnny smith"
    ) id 45
  }

  blockUntilCount(3, "music")

  "reindex" should "reindex all documents from source to target" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    client.sync.reindex("music", "tracks")

    blockUntilCount(3, "tracks")

    val resp = client.sync.execute {
      search in "tracks" query "anderson"
    }
    resp.getHits.totalHits() shouldBe 1
  }

}
