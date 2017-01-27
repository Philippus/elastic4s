package com.sksamuel.elastic4s.searches.sort

import org.elasticsearch.search.sort.SortOrder

case class ScoreSortDefinition(order: SortOrder) extends SortDefinition
