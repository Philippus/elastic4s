package com.sksamuel.elastic4s.http.bulk

import cats.Show
import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, ResponseHandler}
import com.sksamuel.exts.Logging
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait BulkImplicits {

  implicit object BulkShow extends Show[BulkDefinition] {
    override def show(f: BulkDefinition): String = BulkContentBuilder(f).mkString("\n")
  }

  implicit object BulkExecutable extends HttpExecutable[BulkDefinition, BulkResponse] with Logging {

    override def execute(client: RestClient,
                         bulk: BulkDefinition): Future[BulkResponse] = {

      val endpoint = "/_bulk"

      val bulkHttpBody: String = buildBulkHttpBody(bulk)
      if (logger.isDebugEnabled()) {
        logger.debug(s"Bulk entity: {}", bulkHttpBody)
      }
      val entity = new StringEntity(bulkHttpBody, ContentType.APPLICATION_JSON)

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default, skipBodyLogging = true)
    }

    private[bulk] def buildBulkHttpBody(bulk: BulkDefinition): String = {
      val builder = StringBuilder.newBuilder
      val rows: Seq[String] = BulkContentBuilder(bulk)
      rows.addString(builder, "", "\n", "")
      builder.append("\n") // es seems to require a trailing new line as well
      builder.mkString
    }
  }
}
