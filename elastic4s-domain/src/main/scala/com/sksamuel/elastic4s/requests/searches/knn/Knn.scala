package com.sksamuel.elastic4s.requests.searches.knn

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits
import com.sksamuel.elastic4s.requests.searches.queries.{InnerHit, Query}

case class QueryVectorBuilder(
  modelId: String,
  modelText: String)

case class Knn(
     field: String,
     filter: Option[Query] = None,
     k: Option[Int] = None,
     numCandidates: Option[Int] = None,
     queryVector: Seq[Double] = Seq.empty[Double],
     queryVectorBuilder: Option[QueryVectorBuilder] = None,
     similarity: Option[Float] = None,
     boost: Option[Double] = None,
     queryName: Option[String] = None,
     inner: Option[InnerHit] = None) {

  def filter(filter: Query): Knn = copy(filter = filter.some)

  def k(k: Int): Knn = copy(k = k.some)

  def numCandidates(numCandidates: Int): Knn = copy(numCandidates = numCandidates.some)

  def queryVector(queryVector: Seq[Double]): Knn = copy(queryVector = queryVector, queryVectorBuilder = None)

  def queryVectorBuilder(queryVectorBuilder: QueryVectorBuilder): Knn = copy(queryVectorBuilder = queryVectorBuilder.some, queryVector = Seq.empty[Double])

  def similarity(similarity: Float): Knn = copy(similarity = similarity.some)

  def boost(boost: Double): Knn = copy(boost = boost.some)

  def queryName(queryName: String): Knn = copy(queryName = queryName.some)

  def inner(inner: InnerHit): Knn = copy(inner = inner.some)
}

object Knn {
  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double]): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector)

  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double],
             k: Int): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector,
      k = Some(k))

  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double],
             k: Int,
             similarity: Option[Float]): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector,
      k = Some(k),
      similarity = similarity)

  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double],
             k: Int,
             similarity: Option[Float],
             filter: Option[Query]): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector,
      k = Some(k),
      similarity = similarity,
      filter = filter)

  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double],
             k: Int,
             similarity: Option[Float],
             filter: Option[Query],
             boost: Double): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector,
      k = Some(k),
      similarity = similarity,
      filter = filter,
      boost = Some(boost))

  @deprecated("Use the new apply method", "8.15.0")
  def apply(
             field: String,
             numCandidates: Int,
             queryVector: Seq[Double],
             k: Int,
             similarity: Option[Float],
             filter: Option[Query],
             boost: Double,
             inner: Option[InnerHit]): Knn =
    Knn(field = field,
      numCandidates = Some(numCandidates),
      queryVector = queryVector,
      k = Some(k),
      similarity = similarity,
      filter = filter,
      boost = Some(boost),
      inner = inner)
}
