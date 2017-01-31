package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DocumentRef
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.percolator.PercolateQueryBuilder

object PercolateQueryBuilder {
  def apply(q: PercolateQueryDefinition): PercolateQueryBuilder = {
    val builder = q.ref match {
      case Some(DocumentRef(docIndex, docType, docId)) =>
        new PercolateQueryBuilder(q.field, q.`type`, docIndex, docType, docId, null, null, null)
      case _ =>
        q.source.fold(sys.error("Must specify id or source")) { src =>
          new PercolateQueryBuilder(q.field, q.`type`, new BytesArray(src.getBytes))
        }
    }
    builder
  }
}
