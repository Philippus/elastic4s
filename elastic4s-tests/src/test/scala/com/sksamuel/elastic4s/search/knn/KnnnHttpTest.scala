package com.sksamuel.elastic4s.search.knn

import com.sksamuel.elastic4s.fields.{DenseVectorField, DotProduct}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class KnnnHttpTest extends AnyFreeSpec with Matchers with DockerTests with BeforeAndAfterAll {

  private val index = "knn-index"
  override protected def beforeAll() = {
    Try {
      client.execute {
        deleteIndex(index)
      }.await
    }

    client.execute {
      createIndex(index) mapping {
        properties(
          keywordField("name"),
          DenseVectorField(
            name = "vector_field",
            dims = 3,
            index = true,
            similarity = DotProduct)
        )
      }
    }.await

    client.execute {
      bulk(
        indexInto(index) id "1" fields("name" -> "aap", "vector_field" -> Seq(0.2672612419124244, 0.5345224838248488, 0.8017837257372731)),
        indexInto(index) id "1" fields("name" -> "noot", "vector_field" -> Seq(0.4558423058385518, 0.5698028822981898, 0.6837634587578276)),
        indexInto(index) id "1" fields("name" -> "mies", "vector_field" -> Seq(0.5025707110324166, 0.5743665268941904, 0.6461623427559643)),
      ).refresh(RefreshPolicy.Immediate)
    }.await
  }

  "knn" - {
    "should be supported in http client" in {
      val resp = client.execute(
        search(index)
          .query {
            matchAllQuery()
          }
          .knn {
            knnQuery(
              field = "vector_field",
              queryVector = Seq(0.4558423058385518D, 0.5698028822981898D, 0.6837634587578276D)
            ).numCandidates(10)
          }
      ).await.result

      resp.totalHits shouldBe(1)
    }
  }
}
