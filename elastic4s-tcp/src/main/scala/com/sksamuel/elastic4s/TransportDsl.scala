package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.bulk.BulkExecutables
import com.sksamuel.elastic4s.delete.DeleteExecutables
import com.sksamuel.elastic4s.get.GetExecutables
import com.sksamuel.elastic4s.index.{IndexDefinitionExecutable, IndexExecutables}
import com.sksamuel.elastic4s.mappings.MappingExecutables
import com.sksamuel.elastic4s.update.UpdateExecutables

trait TransportDsl
  extends ElasticDsl
    with BulkExecutables
    with DeleteExecutables
    with GetExecutables
    with IndexExecutables
    with MappingExecutables
    with UpdateExecutables {

  implicit val _index = IndexDefinitionExecutable
}

object TransportDsl extends TransportDsl
