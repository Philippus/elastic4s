package com.sksamuel.elastic4s



case class ElasticClientOptions(
  authentication: Authentication
)

object ElasticClientOptions {
  val default: ElasticClientOptions = ElasticClientOptions(
    authentication = Authentication.NoAuth,
  )
}
