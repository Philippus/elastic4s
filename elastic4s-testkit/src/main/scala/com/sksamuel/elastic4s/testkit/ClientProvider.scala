package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.ElasticClient

trait ClientProvider {
  def client: ElasticClient
}
