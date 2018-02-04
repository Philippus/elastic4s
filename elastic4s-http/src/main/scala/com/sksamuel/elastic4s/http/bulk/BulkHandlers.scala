package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.Show
import com.sksamuel.elastic4s.bulk.BulkRequest
import com.sksamuel.elastic4s.http._
import com.sksamuel.exts.Logging
import org.apache.http.entity.ContentType

trait BulkHandlers {

  implicit object BulkHandler extends Handler[BulkRequest, BulkResponse] with Logging {

    override def requestHandler(bulk: BulkRequest): ElasticRequest = {

      val rows = BulkBuilderFn(bulk)
      // es seems to require a trailing new line as well
      val entity = HttpEntity(rows.mkString("\n") + "\n", ContentType.APPLICATION_JSON.getMimeType)
      logger.debug("Sending bulk request")
      logger.debug(rows.mkString("\n"))

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      ElasticRequest("POST", "/_bulk", params.toMap, entity)
    }
  }
}
