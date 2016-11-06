package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.get._
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait MultiGetApi extends GetDsl {

  def multiget(first: GetDefinition, rest: GetDefinition*): MultiGetDefinition = multiget(first +: rest)
  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets.toSeq)

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, MultiGetResult] {
    override def apply(c: Client, t: MultiGetDefinition): Future[MultiGetResult] = {
      injectFutureAndMap(c.multiGet(t.build, _))(MultiGetResult.apply)
    }
  }
}

