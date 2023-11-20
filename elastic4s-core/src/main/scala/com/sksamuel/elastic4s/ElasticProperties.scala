  package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ext.StringOption

object ElasticProperties {

  private val Regex = "(http|https)://(.*?)/?(/.*?)?(\\?.*?)?".r
  private val EndpointRegex = "(.*?)(:\\d+)?".r
  private val ContainsProtocol = "://".r

  /**
    * Creates [[ElasticProperties]] from an URI. The general format of the URI is:
    * http(s)://host:port,host:port(/prefix)?querystring
    *
    * Multiple host:port combinations can be specified, seperated by commas.
    *
    * Options can be specified using standard uri query string syntax, eg cluster.name=superman
    */
  def apply(str: String): ElasticProperties = {
    str match {
      // It's much easier to validate this in a separate check instead of making the main regex much more complex
      case str if ContainsProtocol.findAllMatchIn(str).size > 1 =>
        sys.error(
          s"Invalid uri (multiple protocols) $str, must be in format http(s)://host:port,host:port(/prefix)(?querystr)"
        )

      case Regex(protocol, hoststr, prefix, query) =>
        val hosts = hoststr.split(',').toSeq collect {
          case EndpointRegex(host, port) => ElasticNodeEndpoint(protocol, host, Option(port).map(_.drop(1).toInt).getOrElse(9200), Option(prefix).map(_.stripSuffix("/")))
          case _ => sys.error(s"Invalid hosts/ports $hoststr")
        }
        val options = StringOption(query)
          .map(_.drop(1))
          .map(_.split('&'))
          .getOrElse(Array.empty[String])
          .map(_.split('='))
          .collect {
            case Array(key, value) => (key, value)
            case _ => sys.error(s"Invalid query $query")
          }
        ElasticProperties(hosts, options.toMap)
      case _ =>
        sys.error(
          s"Invalid uri $str, must be in format http(s)://host:port,host:port(/prefix)(?querystr)"
        )
    }
  }
}

/**
  * Contains the endpoints of the nodes to connect to, as well as connection properties.
  */
case class ElasticProperties(endpoints: Seq[ElasticNodeEndpoint], options: Map[String, String] = Map.empty)

/**
  * Holds all of the variables needed to describe the HTTP endpoint of an elasticsearch node.
  *
  * @param protocol http or https
  * @param host     the hostname of the node
  * @param port     the port of the server process
  * @param prefix   an optional prefix that will be prepended to all requests
  */
case class ElasticNodeEndpoint(protocol: String, host: String, port: Int, prefix: Option[String])

