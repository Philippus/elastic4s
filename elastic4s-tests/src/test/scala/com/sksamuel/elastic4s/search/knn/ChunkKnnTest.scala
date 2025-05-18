package com.sksamuel.elastic4s.search.knn

import com.sksamuel.elastic4s.fields.DenseVectorField
import com.sksamuel.elastic4s.requests.common.{FetchSourceContext, RefreshPolicy}
import com.sksamuel.elastic4s.requests.searches.queries.InnerHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class ChunkKnnTest extends AnyFlatSpec with Matchers with DockerTests with BeforeAndAfterAll {

  private val INDEX               = "chunk-knn-index"
  private val FULL_TEXT_FIELD     = "full_text"
  private val CREATION_TIME_FIELD = "creation_time"
  private val PARAGRAPH_FIELD     = "paragraph"
  private val VECTOR_FIELD        = "vector"
  private val TEXT_FIELD          = "text"
  private val PARAGRAPH_ID_FIELD  = "paragraph_id"

  override protected def afterAll() = {
    Try {
      client.execute {
        deleteIndex(INDEX)
      }.await
    }
  }

  override protected def beforeAll() = {
    Try {
      client.execute {
        deleteIndex(INDEX)
      }.await
    }

    client.execute {
      createIndex(INDEX) mapping {
        properties(
          textField(FULL_TEXT_FIELD),
          dateField(CREATION_TIME_FIELD),
          nestedField(PARAGRAPH_FIELD).properties(
            DenseVectorField(
              name = VECTOR_FIELD,
              dims = Some(2),
              index = Some(true)
            ),
            textField(TEXT_FIELD).index(false)
          )
        )
      }
    }.await

    client.execute {
      bulk(
        indexInto(INDEX) id "1" fields (
          FULL_TEXT_FIELD     -> "first paragraph another paragraph",
          CREATION_TIME_FIELD -> "2019-05-04",
          PARAGRAPH_FIELD     -> Seq(
            Map(TEXT_FIELD -> "first paragraph", VECTOR_FIELD   -> Seq(0.45, 45), PARAGRAPH_ID_FIELD -> "1"),
            Map(TEXT_FIELD -> "another paragraph", VECTOR_FIELD -> Seq(0.8, 0.6), PARAGRAPH_ID_FIELD -> "2")
          )
        ),
        indexInto(INDEX) id "2" fields (
          FULL_TEXT_FIELD     -> "number one paragraph number two paragraph",
          CREATION_TIME_FIELD -> "2020-05-04",
          PARAGRAPH_FIELD     -> Seq(
            Map(TEXT_FIELD -> "number one paragraph", VECTOR_FIELD -> Seq(1.2, 4.5), PARAGRAPH_ID_FIELD -> "1"),
            Map(TEXT_FIELD -> "number two paragraph", VECTOR_FIELD -> Seq(-1, 42), PARAGRAPH_ID_FIELD   -> "2")
          )
        )
      ).refresh(RefreshPolicy.Immediate)
    }.await
  }

  "knn search over nested dense_vectors" should "always diversify the top results over the top-level document" in {
    val resp = client.execute(
      search(INDEX)
        .fetchSource(false)
        .sourceInclude(FULL_TEXT_FIELD, CREATION_TIME_FIELD)
        .knn {
          knnQuery(
            field = PARAGRAPH_FIELD + "." + VECTOR_FIELD,
            queryVector = Seq(0.45, 45)
          ).k(2).numCandidates(2)
        }
    ).await.result

    resp.totalHits shouldBe (2)
    resp.hits.hits.map(_.id).toSet shouldBe (Set("1", "2"))
  }

  "knn search with filter" should "always be over the top-level document metadata" in {
    val resp = client.execute(
      search(INDEX)
        .fetchSource(false)
        .sourceInclude(FULL_TEXT_FIELD, CREATION_TIME_FIELD)
        .knn {
          knnQuery(
            field = PARAGRAPH_FIELD + "." + VECTOR_FIELD,
            queryVector = Seq(0.45D, 45D)
          )
            .k(2)
            .numCandidates(2)
            .filter {
              rangeQuery(CREATION_TIME_FIELD)
                .gte("2019-05-01")
                .lte("2019-05-05")
            }
        }
    ).await.result

    resp.totalHits shouldBe (1)
    resp.hits.hits.map(_.id).toSet shouldBe (Set("1"))
  }

  "knn search" should "contain the nearest found paragraph when searching" in {
    val resp = client.execute(
      search(INDEX)
        .fetchSource(false)
        .sourceInclude(FULL_TEXT_FIELD, CREATION_TIME_FIELD)
        .knn {
          knnQuery(
            field = PARAGRAPH_FIELD + "." + VECTOR_FIELD,
            queryVector = Seq(0.45D, 45D)
          ).k(2)
            .numCandidates(2)
            .inner(InnerHit(PARAGRAPH_FIELD)
              .fetchSource(FetchSourceContext(fetchSource = false, includes = Set(PARAGRAPH_FIELD + "." + TEXT_FIELD)))
              .size(1)
              .fields(Seq(PARAGRAPH_FIELD + "." + TEXT_FIELD)))
        }
    ).await.result

    resp.totalHits shouldBe (2)
    resp.hits.hits.map(_.id).toSet shouldBe (Set("1", "2"))
    resp.hits.hits.map(_.innerHits.get(PARAGRAPH_FIELD).fold(Seq.empty[String]) {
      _.hits.flatMap { hit =>
        val texts = hit
          .docValueFieldOpt(PARAGRAPH_FIELD)
          .fold[Seq[String]](Seq.empty) {
            _.values.flatMap { v =>
              Try {
                v.asInstanceOf[Map[String, Seq[String]]]
                  .getOrElse(TEXT_FIELD, Seq.empty)
              }.getOrElse(Seq.empty)
            }
          }
        texts
      }
    }).toSet shouldBe Set(List("first paragraph"), List("number two paragraph"))
  }
}
