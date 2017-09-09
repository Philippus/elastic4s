package com.sksamuel.elastic4s

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration

trait AbstractURLParameterDefinition {
  def timeout: Option[FiniteDuration] = None

  def refresh: Option[RefreshPolicy] = None

  def requestsPerSecond: Option[Float] = None

  def waitForActiveShards: Option[Int] = None

  def waitForCompletion: Option[Boolean] = None
}

case class BaseURLParameterDefinition(override val timeout: Option[FiniteDuration] = None,
                                      override val refresh: Option[RefreshPolicy] = None,
                                      override val requestsPerSecond: Option[Float] = None,
                                      override val waitForActiveShards: Option[Int] = None,
                                      override val waitForCompletion: Option[Boolean] = None)
  extends AbstractURLParameterDefinition {

  import com.sksamuel.exts.OptionImplicits._

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
