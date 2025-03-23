package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticClient

import scala.concurrent.Future

trait FutureClientProvider {
  def client: ElasticClient[Future]
}
