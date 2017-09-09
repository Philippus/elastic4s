package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.AbstractURLParameterDefinition
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicNameValuePair
import org.elasticsearch.common.unit.TimeValue

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

object EncodeURLParameters {

  import scala.collection.JavaConverters._

  implicit def apply(request: AbstractURLParameterDefinition): String = {
    val parameters = ListBuffer.empty[NameValuePair]

    request.timeout.map(_.toNanos).map(TimeValue.timeValueNanos)
      .foreach { timeout =>
        parameters += new BasicNameValuePair("timeout", s"${timeout}ns")
      }
    request.requestsPerSecond
      .foreach { requestsPerSecond =>
        parameters += new BasicNameValuePair("requests_per_second", requestsPerSecond.toString)
      }
    request.waitForActiveShards
      .foreach { waitForActiveShards =>
        parameters += new BasicNameValuePair("wait_for_active_shards", waitForActiveShards.toString)
      }
    request.waitForCompletion
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
