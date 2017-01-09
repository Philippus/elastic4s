package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait BulkExecutables {

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, RichBulkResponse] {
    override def apply(c: Client, t: BulkDefinition): Future[RichBulkResponse] = {
      injectFutureAndMap(c.bulk(t.build, _))(RichBulkResponse.apply)
    }
  }

}
