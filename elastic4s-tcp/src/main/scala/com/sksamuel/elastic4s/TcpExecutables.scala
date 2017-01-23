package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.alias.AliasExecutables
import com.sksamuel.elastic4s.bulk.BulkExecutables
import com.sksamuel.elastic4s.delete.DeleteExecutables
import com.sksamuel.elastic4s.explain.ExplainExecutables
import com.sksamuel.elastic4s.get.GetExecutables
import com.sksamuel.elastic4s.index.{CreateIndexExecutables, IndexExecutables}
import com.sksamuel.elastic4s.mappings.MappingExecutables
import com.sksamuel.elastic4s.reindex.ReindexExecutables
import com.sksamuel.elastic4s.search.{ScrollExecutables, SearchExecutables}
import com.sksamuel.elastic4s.task.TaskExecutables
import com.sksamuel.elastic4s.termvectors.TermVectorsExecutables
import com.sksamuel.elastic4s.update.UpdateExecutables
import com.sksamuel.elastic4s.validate.ValidateExecutables

trait TcpExecutables
  extends AliasExecutables
    with BulkExecutables
    with CreateIndexExecutables
    with DeleteExecutables
    with ExplainExecutables
    with GetExecutables
    with IndexExecutables
    with MappingExecutables
    with ReindexExecutables
    with ScrollExecutables
    with SearchExecutables
    with TaskExecutables
    with TermVectorsExecutables
    with UpdateExecutables
    with ValidateExecutables {
  self: ElasticDsl =>
}
