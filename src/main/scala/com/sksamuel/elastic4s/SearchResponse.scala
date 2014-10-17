package com.sksamuel.elastic4s

import org.elasticsearch.action.search.{SearchResponse => JavaResponse}

import scala.concurrent.duration._

class SearchResponse(java: JavaResponse) {

  def aggregations = java.getAggregations
  def facets = java.getFacets
  def failedShards = java.getFailedShards
  def hits = java.getHits
  def scrollId = java.getScrollId
  def successfulShards = java.getSuccessfulShards
  def suggest = java.getSuggest
  def took: FiniteDuration = java.getTookInMillis.millis
  def totalShards = java.getTotalShards
  def context = java.getContext
}
