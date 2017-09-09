package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.URLParameters
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicNameValuePair

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

object EncodeURLParameters {

  import scala.collection.JavaConverters._

  implicit def apply(urlParamsOption: Option[URLParameters]): String =
    urlParamsOption match {
      case Some(u) => apply(u)
      case None => ""
    }

  implicit def apply(urlParams: URLParameters): String = {
    val parameters = ListBuffer.empty[NameValuePair]

    urlParams.timeout.map(_.toMillis)
      .foreach { timeout =>
        parameters += new BasicNameValuePair("timeout", s"${timeout}ms")
      }
    urlParams.requestsPerSecond
      .foreach { requestsPerSecond =>
        parameters += new BasicNameValuePair("requests_per_second", requestsPerSecond.toString)
      }
    urlParams.waitForActiveShards
      .foreach { waitForActiveShards =>
        parameters += new BasicNameValuePair("wait_for_active_shards", waitForActiveShards.toString)
      }
    urlParams.waitForCompletion
      .foreach { waitForCompletion =>
        parameters += new BasicNameValuePair("wait_for_completion", waitForCompletion.toString)
      }

    if (parameters.nonEmpty) {
      "?" + URLEncodedUtils.format(
        parameters.asJava,
        "UTF-8"
      )
    } else {
      ""
    }
  }
}
