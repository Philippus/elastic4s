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

      val rows = BulkContentBuilder(bulk)
      logger.debug(s"Bulk entity: ${rows.mkString("\n")}")
      // es seems to require a trailing new line as well
      val entity = new StringEntity(rows.mkString("\n") + "\n", ContentType.APPLICATION_JSON)

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}
