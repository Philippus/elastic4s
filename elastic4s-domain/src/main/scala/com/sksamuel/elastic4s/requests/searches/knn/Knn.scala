package com.sksamuel.elastic4s.requests.searches.knn

case class Knn(
     field: String,
     numCandidates: Int,
     queryVector: Seq[Double],
     k: Int = 1,
     similarity: Option[Float] = None,
     boost: Double = 1.0
)
