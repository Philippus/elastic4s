package com.sksamuel.elastic4s.http.bulk

import cats.Show
import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.http.values.RefreshPolicyHttpValue
import com.sksamuel.exts.Logging
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future

trait BulkImplicits {

  implicit object BulkShow extends Show[BulkDefinition] {
    override def show(f: BulkDefinition): String = BulkBuilderFn(f).mkString("\n")
  }

  implicit object BulkExecutable extends HttpExecutable[BulkDefinition, BulkResponse] with Logging {

    override def execute(client: RestClient, bulk: BulkDefinition): Future[Response] = {

      val rows = BulkBuilderFn(bulk)
      // es seems to require a trailing new line as well
      val entity = new StringEntity(rows.mkString("\n") + "\n", ContentType.APPLICATION_JSON)

      val params = scala.collection.mutable.Map.empty[String, String]
      bulk.timeout.foreach(params.put("timeout", _))
      bulk.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))

      client.async("POST", "/_bulk", params.toMap, entity)
    }
  }
}
