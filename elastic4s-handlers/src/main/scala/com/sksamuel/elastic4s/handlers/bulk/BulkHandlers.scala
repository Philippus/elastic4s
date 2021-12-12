package com.sksamuel.elastic4s.handlers.bulk

import com.sksamuel.elastic4s.requests.bulk.{BulkRequest, BulkResponse}
import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

object BulkHandlers extends BulkHandlers

trait BulkHandlers {

  implicit object BulkHandler extends Handler[BulkRequest, BulkResponse] {

    override def build(bulk: BulkRequest): ElasticRequest = {
      val httpBody: String = buildBulkHttpBody(bulk)
      val entity = HttpEntity(httpBody, "application/x-ndjson")
      if (logger.isDebugEnabled()) {
        logger.debug("Sending bulk request")
        logger.debug(httpBody)
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      ElasticRequest("POST", "/_bulk", params.toMap, entity)
    }
  }

  private[bulk] def buildBulkHttpBody(bulk: BulkRequest): String = {
    val builder = StringBuilder.newBuilder
    val rows: Seq[String] = BulkBuilderFn(bulk)
    rows.addString(builder, "", "\n", "")
    builder.append("\n") // es seems to require a trailing new line as well
    builder.mkString
  }
}
