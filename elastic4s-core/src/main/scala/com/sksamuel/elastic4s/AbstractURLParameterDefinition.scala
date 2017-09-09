package com.sksamuel.elastic4s

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration


abstract class AbstractURLParameterDefinition(val timeout: Option[FiniteDuration] = None,
                                              val refresh: Option[RefreshPolicy] = None,
                                              val requestsPerSecond: Option[Float] = None,
                                              val waitForActiveShards: Option[Int] = None,
                                              val waitForCompletion: Option[Boolean] = None
                                             )

case class BasicURLParameterDefinition(override val timeout: Option[FiniteDuration] = None,
                                       override val refresh: Option[RefreshPolicy] = None,
                                       override val requestsPerSecond: Option[Float] = None,
                                       override val waitForActiveShards: Option[Int] = None,
                                       override val waitForCompletion: Option[Boolean] = None)
  extends AbstractURLParameterDefinition {

  import com.sksamuel.exts.OptionImplicits._

  def timeout(timeout: FiniteDuration): BasicURLParameterDefinition =
    copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): BasicURLParameterDefinition =
    copy(refresh = refresh.some)

  def requestsPerSecond(requestsPerSecond: Float): BasicURLParameterDefinition =
    copy(requestsPerSecond = requestsPerSecond.some)

  def waitForActiveShards(waitForActiveShards: Int): BasicURLParameterDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForCompletion(waitForCompletion: Boolean): BasicURLParameterDefinition =
    copy(waitForCompletion = waitForCompletion.some)
}
