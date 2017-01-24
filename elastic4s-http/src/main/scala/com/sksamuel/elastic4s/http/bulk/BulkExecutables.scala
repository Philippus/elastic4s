package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, Shards}
import com.sksamuel.exts.Logging
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

case class Index(_index: String, _type: String, _id: String, version: Long, result: String, forced_result: Boolean, _shards: Shards)

case class BulkResponseItem(index: Index)

case class BulkResponse(took: Long, errors: Boolean, items: Seq[BulkResponseItem])

trait BulkExecutables {

  implicit object BulkExecutable extends HttpExecutable[BulkDefinition, BulkResponse] with Logging {
    override def execute(client: RestClient, bulk: BulkDefinition): (ResponseListener) => Any = {

      val endpoint = "/_bulk"

      val rows = BulkEntityBuilder(bulk)
      logger.info(s"Bulk entity: ${rows.mkString("\n")}")
      // es seems to require a trailing new line as well
      val entity = new StringEntity(rows.mkString("\n") + "\n")

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      client.performRequestAsync("POST", endpoint, params.asJava, entity, _)
    }
  }
}
