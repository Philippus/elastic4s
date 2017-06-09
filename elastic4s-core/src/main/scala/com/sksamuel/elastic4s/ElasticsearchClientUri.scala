package com.sksamuel.elastic4s

import com.sksamuel.exts.StringOption

import scala.language.implicitConversions

object ElasticsearchClientUri {

  private val Regex = "elasticsearch://(.*?)(\\?.*?)?".r

  implicit def stringtoUri(str: String): ElasticsearchClientUri = ElasticsearchClientUri(str)

  /**
    * Creates an ElasticsearchClientUri from a single host and port with no options.
    */
  def apply(host: String, port: Int): ElasticsearchClientUri = apply(s"elasticsearch://$host:$port")

  def apply(str: String): ElasticsearchClientUri = {
    str match {
      case Regex(hoststr, query) =>
        val hosts = hoststr.split(',').map(_.split(':')).map {
          case Array(host, port) => (host, port.toInt)
          case _ => sys.error(s"Invalid hosts/ports $hoststr")
        }
        val options = StringOption(query)
          .map(_.drop(1))
          .map(_.split('&')).getOrElse(Array.empty)
          .map(_.split('=')).collect {
          case Array(key, value) => (key, value)
          case _ => sys.error(s"Invalid query $query")
        }
        ElasticsearchClientUri(str, hosts.toList, options.toMap)
      case _ => sys.error("Invalid uri, must be in format elasticsearch://host:port,host:port?querystr")
    }
  }
}

/**
  * Uri used to connect to an Elasticsearch cluster. The general format is
  *
  * elasticsearch://host:port,host:port?querystring
  *
  * Multiple host:port combinations can be specified, seperated by commas.
  * Options can be specified using standard uri query string syntax, eg cluster.name=superman
  *
  * To use HTTPS when using the HTTP client, add ssl=true to the query parameters.
  */
case class ElasticsearchClientUri(uri: String, hosts: List[(String, Int)], options: Map[String, String] = Map.empty)
