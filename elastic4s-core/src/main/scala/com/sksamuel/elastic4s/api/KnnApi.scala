package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.knn.Knn

trait KnnApi {
  def knnQuery(field: String, numCandidates: Int, vector: Seq[Double]): Knn =
    Knn(
      field = field,
      queryVector = vector,
      numCandidates = Some(numCandidates)
    )
}
