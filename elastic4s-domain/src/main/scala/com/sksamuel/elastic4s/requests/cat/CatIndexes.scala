package com.sksamuel.elastic4s.requests.cat

import com.sksamuel.elastic4s.requests.common.HealthStatus

case class CatIndexes(health: Option[HealthStatus], indexPattern: Option[String])
