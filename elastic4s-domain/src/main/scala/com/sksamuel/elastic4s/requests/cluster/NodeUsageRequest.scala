package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.ext.OptionImplicits._

import scala.concurrent.duration.Duration

case class NodeUsageRequest(nodeId: Option[String] = None,
                            // Specifies the period of time to wait for a connection to the master node
                            masterTimeout: Option[String] = None,
                            // Specifies the period of time to wait for a response
                            timeout: Option[String] = None) {

  def nodeId(nodeId: String): NodeUsageRequest = copy(nodeId = nodeId.some)

  def masterTimeout(timeout: Duration): NodeUsageRequest = copy(masterTimeout = (timeout.toNanos + "n").some)
  def masterTimeout(timeout: String): NodeUsageRequest = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): NodeUsageRequest = copy(timeout = (timeout.toNanos + "n").some)
  def timeout(timeout: String): NodeUsageRequest = copy(timeout = timeout.some)
}
