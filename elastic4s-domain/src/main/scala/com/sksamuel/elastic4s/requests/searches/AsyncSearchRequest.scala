package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class AsyncSearchRequest(innerRequest: SearchRequest,
                              keepOnCompletion: Option[Boolean] = None,
                              waitForCompletionTimeout: Option[Int] = None,
                              keepAlive: Option[String] = None) {

  def keepOnCompletion(enabled: Boolean): AsyncSearchRequest = copy(keepOnCompletion = enabled.some)
  def waitForCompletionTimeout(seconds: Int): AsyncSearchRequest = copy(waitForCompletionTimeout = seconds.some)
  def keepAlive(duration: String): AsyncSearchRequest = copy(keepAlive = duration.some)
}
