package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.knn.KnnBuilderFn
import com.sksamuel.elastic4s.requests.searches.HighlightField
import com.sksamuel.elastic4s.requests.searches.knn.{Knn, QueryVectorBuilder}
import com.sksamuel.elastic4s.requests.searches.sort.FieldSort
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class KnnBuilderFnTest extends AnyFunSuite with Matchers {

  test("Basic Knn request generates proper query.") {
    val request = Knn("image-vector").numCandidates(50).queryVector(Seq(54D, 10D, -2D))
    KnnBuilderFn(request).string shouldBe
      """{"field":"image-vector","num_candidates":50,"query_vector":[54.0,10.0,-2.0]}""".stripMargin
  }
  test("Knn supports queryName") {
    val request = Knn("image-vector").numCandidates(50).queryVector(Seq(54D, 10D, -2D)).queryName("abc")
    KnnBuilderFn(request).string shouldBe
      """{"field":"image-vector","num_candidates":50,"query_vector":[54.0,10.0,-2.0],"_name":"abc"}""".stripMargin
  }
  test("Knn with all fields generates proper query.") {
    val request = Knn("image-vector") numCandidates 50 queryVector Seq(54D, 10D, -2D) k 5 filter TermQuery(
      "file-type",
      "png"
    ) similarity 10 boost .4
    KnnBuilderFn(request).string shouldBe
      """{"field":"image-vector","filter":{"term":{"file-type":{"value":"png"}}},"k":5,"num_candidates":50,"query_vector":[54.0,10.0,-2.0],"similarity":10.0,"boost":0.4}""".stripMargin
  }
  test("Knn with inner_hits generates proper query.") {
    val innerHit = InnerHit("inners")
      .from(2)
      .explain(false)
      .trackScores(true)
      .version(true)
      .size(2)
      .docValueFields(List("df1", "df2"))
      .sortBy(FieldSort("sortField"))
      .storedFieldNames(List("field1", "field2"))
      .highlighting(HighlightField("hlField"))
      .fields(List("f1", "f2"))

    val request =
      Knn("image-vector") numCandidates 50 queryVector Seq(54D, 10D, -2D) inner innerHit k 5 filter TermQuery(
        "file-type",
        "png"
      ) similarity 10 boost .4
    KnnBuilderFn(request).string shouldBe
      """{"field":"image-vector","filter":{"term":{"file-type":{"value":"png"}}},"k":5,"num_candidates":50,"query_vector":[54.0,10.0,-2.0],"similarity":10.0,"boost":0.4,"inner_hits":{"name":"inners","from":2,"explain":false,"track_scores":true,"version":true,"size":2,"docvalue_fields":["df1","df2"],"sort":[{"sortField":{"order":"asc"}}],"stored_fields":["field1","field2"],"fields":["f1","f2"],"highlight":{"fields":{"hlField":{}}}}}"""
  }

  test("knn supports query vector builder") {
    val request = Knn(
      field = "dense-vector-field",
      k = Some(10),
      numCandidates = Some(100),
      queryVectorBuilder = Some(QueryVectorBuilder("my-text-embedding-model", "The opposite of blue"))
    )

    KnnBuilderFn(request).string shouldBe
      """{"field":"dense-vector-field","k":10,"num_candidates":100,"query_vector_builder":{"text_embedding":{"model_id":"my-text-embedding-model","model_text":"The opposite of blue"}}}"""
  }

  test("knn prefers query vector builder over query vector") {
    val request = Knn(
      field = "dense-vector-field",
      k = Some(10),
      numCandidates = Some(100),
      queryVector = Seq(54D, 10D, -2D),
      queryVectorBuilder = Some(QueryVectorBuilder("my-text-embedding-model", "The opposite of blue"))
    )

    KnnBuilderFn(request).string shouldBe
      """{"field":"dense-vector-field","k":10,"num_candidates":100,"query_vector_builder":{"text_embedding":{"model_id":"my-text-embedding-model","model_text":"The opposite of blue"}}}"""
  }

  test("knn query vector clears query vector builder") {
    val request = Knn(
      field = "dense-vector-field",
      k = Some(10),
      numCandidates = Some(100),
      queryVectorBuilder = Some(QueryVectorBuilder("my-text-embedding-model", "The opposite of blue"))
    ) queryVector Seq(54D, 10D, -2D)

    KnnBuilderFn(request).string shouldBe
      """{"field":"dense-vector-field","k":10,"num_candidates":100,"query_vector":[54.0,10.0,-2.0]}"""
  }

  test("knn query vector builder clears query vector") {
    val request = Knn(
      field = "dense-vector-field",
      k = Some(10),
      numCandidates = Some(100),
      queryVector = Seq(54D, 10D, -2D)
    ) queryVectorBuilder QueryVectorBuilder("my-text-embedding-model", "The opposite of blue")

    KnnBuilderFn(request).string shouldBe
      """{"field":"dense-vector-field","k":10,"num_candidates":100,"query_vector_builder":{"text_embedding":{"model_id":"my-text-embedding-model","model_text":"The opposite of blue"}}}"""
  }
}
