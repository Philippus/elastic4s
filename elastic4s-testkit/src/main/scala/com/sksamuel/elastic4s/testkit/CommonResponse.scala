package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.Hit
import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.http.bulk.BulkResponse
import com.sksamuel.elastic4s.http.index.IndexResponse
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.RichSearchResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse

// A lot (or all of this) might not be needed, depending on how much sharing is done with common traits between TCP and HTTP.
// Also, the Units are just placeholders for now.

case class CommonSearchResponse(totalHits: Long,
                                hits: IndexedSeq[Hit])

trait CommonResponse[T, R] {
  def convert(response: T): R
}

object CommonResponseImplicits {
  import com.sksamuel.elastic4s.http.search.SearchResponse

  implicit object TcpIndexResponse extends CommonResponse[RichIndexResponse, Unit] {
    override def convert(response: RichIndexResponse) = ()
  }

  implicit object HttpIndexResponse extends CommonResponse[IndexResponse, Unit] {
    override def convert(response: IndexResponse) = ()
  }

  implicit object TcpCreateIndexResponse extends CommonResponse[CreateIndexResponse, Unit] {
    override def convert(response: CreateIndexResponse) = ()
  }

  implicit object HttpCreateIndexResponse extends CommonResponse[com.sksamuel.elastic4s.http.index.CreateIndexResponse, Unit] {
    override def convert(response: com.sksamuel.elastic4s.http.index.CreateIndexResponse) = ()
  }

  implicit object HttpBulkResponse extends CommonResponse[BulkResponse, Unit] {
    override def convert(response: BulkResponse) = ()
  }

  implicit object TcpBulkResponse extends CommonResponse[RichBulkResponse, Unit] {
    override def convert(response: RichBulkResponse) = ()
  }

  implicit object TcpSearchResponse extends CommonResponse[RichSearchResponse, CommonSearchResponse] {
    override def convert(response: RichSearchResponse) = CommonSearchResponse(response.totalHits, response.hits)
  }

  implicit object HttpSearchResponse extends CommonResponse[SearchResponse, CommonSearchResponse] {
    override def convert(response: SearchResponse) = CommonSearchResponse(response.totalHits, response.hits.hits)
  }
}
