package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.percolator.PercolateQueryBuilder

case class PercolateQueryDefinition(field: String,
                                    `type`: String,
                                    ref: Option[DocumentRef] = None,
                                    source: Option[String] = None) {

  def builder = {
    val builder = ref match {
      case Some(DocumentRef(docIndex, docType, docId)) =>
        new PercolateQueryBuilder(field, `type`, docIndex, docType, docId, null, null, null)
      case _ =>
        new PercolateQueryBuilder(field, `type`, new BytesArray(source.get.getBytes))
      case _ =>
        sys.error("Must specify id or item")
    }
    builder
  }
}
