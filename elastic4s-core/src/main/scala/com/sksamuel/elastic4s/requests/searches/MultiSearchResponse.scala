package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.HitReader

import scala.util.Try

case class SearchError(`type`: String,
                       `reason`: String,
                       @JsonProperty("resource.type") resourceType: String,
                       @JsonProperty("resource.id") resourceId: String,
                       index_uuid: String,
                       index: String)

case class MultisearchResponseItem(index: Int, status: Int, response: Either[SearchError, SearchResponse])

case class MultiSearchResponse(items: Seq[MultisearchResponseItem]) {

  def size: Int = items.size

  def failures: Seq[SearchError] = items.map(_.response).collect {
    case left: Left[SearchError, SearchResponse] => left.left.get
  }

  def successes: Seq[SearchResponse] = items.map(_.response).collect {
    case right: Right[SearchError, SearchResponse] => right.right.get
  }

  def to[T: HitReader]: IndexedSeq[T] = successes.flatMap(_.hits.hits).map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = successes.flatMap(_.hits.hits).map(_.safeTo[T]).toIndexedSeq
}
