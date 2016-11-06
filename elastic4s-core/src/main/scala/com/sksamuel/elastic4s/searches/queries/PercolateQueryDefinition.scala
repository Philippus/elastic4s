package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.percolator.PercolateQueryBuilder

case class PercolateQueryDefinition(field: String,
                                    `type`: String,
                                    ref: Option[DocumentRef] = None,
                                    source: Option[String] = None) extends QueryDefinition {

  def builder: PercolateQueryBuilder = {

    val builder = ref match {
      case Some(DocumentRef(docIndex, docType, docId)) =>
        new PercolateQueryBuilder(field, `type`, docIndex, docType, docId, null, null, null)
      case _ =>
        source.fold(sys.error("Must specify id or source")) { src =>
          new PercolateQueryBuilder(field, `type`, new BytesArray(src.getBytes))
        }
    }
    builder
  }
}
