package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexAdminDsl {
  implicit object OpenIndexDefinitionExecutable extends Executable[OpenIndexDefinition, OpenIndexResponse] {
    override def apply(c: Client, t: OpenIndexDefinition): Future[OpenIndexResponse] = {
      injectFuture(c.admin.indices.prepareOpen(t.index).execute)
    }
  }
  implicit object CloseIndexDefinitionExecutable extends Executable[CloseIndexDefinition, CloseIndexResponse] {
    override def apply(c: Client, t: CloseIndexDefinition): Future[CloseIndexResponse] = {
      injectFuture(c.admin.indices.prepareClose(t.index).execute)
    }
  }
  implicit object GetSegmentsDefinitionExecutable extends Executable[GetSegmentsDefinition, IndicesSegmentResponse] {
    override def apply(c: Client, t: GetSegmentsDefinition): Future[IndicesSegmentResponse] = {
      injectFuture(c.admin.indices.prepareSegments(t.indexes: _*).execute)
    }
  }
  implicit object IndexExistsDefinitionExecutable extends Executable[IndexExistsDefinition, IndicesExistsResponse] {
    override def apply(c: Client, t: IndexExistsDefinition): Future[IndicesExistsResponse] = {
      injectFuture(c.admin.indices.prepareExists(t.indexes: _*).execute)
    }
  }
  implicit object TypesExistsDefinitionExecutable extends Executable[TypesExistsDefinition, TypesExistsResponse] {
    override def apply(c: Client, t: TypesExistsDefinition): Future[TypesExistsResponse] = {
      injectFuture(c.admin.indices.prepareTypesExists(t.indexes: _*).setTypes(t.types: _*).execute)
    }
  }
  implicit object FlushIndexDefinitionExecutable extends Executable[FlushIndexDefinition, FlushResponse] {
    override def apply(c: Client, t: FlushIndexDefinition): Future[FlushResponse] = {
      injectFuture(c.admin.indices.prepareFlush(t.indexes: _*).execute)
    }
  }
}

class OpenIndexDefinition(val index: String)
class CloseIndexDefinition(val index: String)
class GetSegmentsDefinition(val indexes: Seq[String])
class FlushIndexDefinition(val indexes: Seq[String])
class IndexExistsDefinition(val indexes: Seq[String])
class TypesExistsDefinition(val indexes: Seq[String], val types: Seq[String])
