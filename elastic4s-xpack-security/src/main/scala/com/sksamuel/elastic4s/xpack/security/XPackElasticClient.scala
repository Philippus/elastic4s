package com.sksamuel.elastic4s.xpack.security

import java.net.InetSocketAddress

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri, ElasticClient$}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient

object XPackElasticClient {

  def apply(settings: Settings,
            uri: ElasticsearchClientUri,
            plugins: Class[_ <: Plugin]*): ElasticClient = {

    val combinedSettings = uri.options.foldLeft(Settings.builder().put(settings)) { (builder, kv) =>
      if (builder.get(kv._1) == null)
        builder.put(kv._1, kv._2)
      builder
    }.build()

    val client = new PreBuiltXPackTransportClient(combinedSettings, plugins: _*)
    for ( (host, port) <- uri.hosts ) {
      client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
    }
    ElasticClient.fromClient(client)
  }
}
