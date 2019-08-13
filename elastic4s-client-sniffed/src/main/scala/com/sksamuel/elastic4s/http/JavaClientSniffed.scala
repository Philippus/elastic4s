package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.JavaClient.fromRestClient
import com.sksamuel.elastic4s.{ElasticNodeEndpoint, ElasticProperties}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}
import org.elasticsearch.client.sniff.{NodesSniffer, SniffOnFailureListener, Sniffer}

import scala.concurrent.duration.{FiniteDuration, _}

/**
  *
  * @param sniffIntervals Sets the interval between consecutive ordinary sniff executions in milliseconds. Will be honoured when
  *                                  sniffOnFailure is disabled or when there are no failures between consecutive sniff executions.
  * @param sniffAfterFailureInterval Sets the delay of a sniff execution scheduled after a failure (in milliseconds), when not set,
  *                                  no sniffing after failure is performed
  * @param nodeSniffer               Sets the [[org.elasticsearch.client.sniff.NodesSniffer]] to be used to read hosts.
  *                                  A default instance of [[org.elasticsearch.client.sniff.ElasticsearchNodesSniffer]]
  *                                  is created when not provided. This method can be used to change the configuration of the
  *                                  [[org.elasticsearch.client.sniff.ElasticsearchNodesSniffer]],
  *                                  or to provide a different implementation (e.g. in case hosts need to taken from a different source).
  */
case class SniffingConfiguration(sniffIntervals: FiniteDuration = 5.minutes,
                                 sniffAfterFailureInterval: Option[FiniteDuration] = Some(1.minute),
                                 nodeSniffer: Option[NodesSniffer] = None)

object JavaClientSniffed extends Logging {

  /**
    * Creates a new [[com.sksamuel.elastic4s.ElasticClient]] using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    * Sniffing is added by the [[SniffingConfiguration]]
    */
  def apply(props: ElasticProperties, sniffingConfiguration: SniffingConfiguration): JavaClient =
    apply(props, NoOpRequestConfigCallback, NoOpHttpClientConfigCallback, sniffingConfiguration)

  /**
    * Creates a new [[com.sksamuel.elastic4s.ElasticClient]] using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    * Sniffing is added by the [[SniffingConfiguration]]
    */
  def apply(props: ElasticProperties,
            requestConfigCallback: RequestConfigCallback,
            httpClientConfigCallback: HttpClientConfigCallback,
            sniffingConfiguration: SniffingConfiguration): JavaClient = {
    val hosts = props.endpoints.map {
      case ElasticNodeEndpoint(protocol, host, port, _) => new HttpHost(host, port, protocol)
    }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")

    import sniffingConfiguration._

    lazy val failureSniffer = new SniffOnFailureListener()

    val clientBuilder = RestClient
      .builder(hosts: _*)
      .setRequestConfigCallback(requestConfigCallback)
      .setHttpClientConfigCallback(httpClientConfigCallback)

    val client =
      sniffAfterFailureInterval.fold(clientBuilder)(_ => clientBuilder.setFailureListener(failureSniffer)).build()

    val snifferBuilder =
      Sniffer.builder(client).setSniffIntervalMillis(sniffIntervals.toMillis.toInt)

    val builderWithNodeSniffer = nodeSniffer.fold(snifferBuilder)(snifferBuilder.setNodesSniffer)

    val builderWithFaulureInterval = sniffAfterFailureInterval
      .map(_.toMillis.toInt)
      .fold(builderWithNodeSniffer)(builderWithNodeSniffer.setSniffAfterFailureDelayMillis)

    val sniffer = builderWithFaulureInterval.build()

    if (sniffAfterFailureInterval.isDefined) failureSniffer.setSniffer(sniffer)

    fromRestClient(client)
  }
}
