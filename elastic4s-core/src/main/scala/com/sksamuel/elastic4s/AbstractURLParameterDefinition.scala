package com.sksamuel.elastic4s

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration


abstract class AbstractURLParameterDefinition(val timeout: Option[FiniteDuration] = None,
                                              val refresh: Option[RefreshPolicy] = None,
                                              val requestsPerSecond: Option[Float] = None,
                                              val waitForActiveShards: Option[Int] = None,
                                              val waitForCompletion: Option[Boolean] = None
                                             )

case class BaseURLParameterDefinition(override val timeout: Option[FiniteDuration] = None,
                                      override val refresh: Option[RefreshPolicy] = None,
                                      override val requestsPerSecond: Option[Float] = None,
                                      override val waitForActiveShards: Option[Int] = None,
                                      override val waitForCompletion: Option[Boolean] = None)
  extends AbstractURLParameterDefinition {

  def timeout(timeout: FiniteDuration): BaseURLParameterDefinition =
    copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): BaseURLParameterDefinition =
    copy(refresh = refresh.some)

  def requestsPerSecond(requestsPerSecond: Float): BaseURLParameterDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def waitForActiveShards(waitForActiveShards: Int): BaseURLParameterDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForCompletion(waitForCompletion: Boolean): BaseURLParameterDefinition =
    copy(waitForCompletion = waitForCompletion.some)
}
