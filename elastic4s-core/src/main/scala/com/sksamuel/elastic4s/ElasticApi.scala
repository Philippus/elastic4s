package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.alias.AliasesApi
import com.sksamuel.elastic4s.bulk.BulkApi
import com.sksamuel.elastic4s.delete.DeleteApi
import com.sksamuel.elastic4s.explain.ExplainApi
import com.sksamuel.elastic4s.get.GetApi
import com.sksamuel.elastic4s.indexes.IndexApi
import com.sksamuel.elastic4s.script.ScriptApi
import com.sksamuel.elastic4s.task.TaskApi
import com.sksamuel.elastic4s.update.UpdateApi

// contains all the syntactic definitions
trait ElasticApi
  extends AliasesApi
    with BulkApi
    with DeleteApi
    with ExplainApi
    with GetApi
    with IndexApi
    with ScriptApi
    with TaskApi
    with UpdateApi
