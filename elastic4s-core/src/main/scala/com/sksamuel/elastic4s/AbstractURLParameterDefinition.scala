package com.sksamuel.elastic4s

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration

abstract class AbstractURLParameterDefinition(val timeout: Option[FiniteDuration] = None,
                                              val refresh: Option[RefreshPolicy] = None,
                                              val requestsPerSecond: Option[Float] = None,
                                              val waitForActiveShards: Option[Int] = None,
                                              val waitForCompletion: Option[Boolean] = None
                                             )
