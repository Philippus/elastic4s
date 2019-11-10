package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.{ElasticError, HitReader}

import scala.util.Try


case class MultisearchResponseItem(index: Int, status: Int, response: Either[ElasticError, SearchResponse])

case class MultiSearchResponse(items: Seq[MultisearchResponseItem]) {

  def size: Int = items.size

  def failures: Seq[ElasticError] = items.map(_.response).collect {
    case left: Left[ElasticError, SearchResponse] => left.left.get
  }

  def successes: Seq[SearchResponse] = items.map(_.response).collect {
    case right: Right[ElasticError, SearchResponse] => right.right.get
  }

  def to[T: HitReader]: IndexedSeq[T] = successes.flatMap(_.hits.hits).map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = successes.flatMap(_.hits.hits).map(_.safeTo[T]).toIndexedSeq
}
