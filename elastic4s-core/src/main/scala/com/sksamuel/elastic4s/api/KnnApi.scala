package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.knn.{Knn, QueryVectorBuilder}

trait KnnApi {
  def knnQuery(field: String, queryVector: Seq[Double]): Knn =
    Knn(field = field, queryVector = queryVector)

  def knnQuery(field: String, queryVectorBuilder: QueryVectorBuilder): Knn =
    Knn(field = field, queryVectorBuilder = Some(queryVectorBuilder))
}
