package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder

object FilterFunctionBuilderFn {
  def apply(func: ScoreFunctionDefinition): FilterFunctionBuilder = {
    func.filter match {
      case Some(q) => new FilterFunctionBuilder(QueryBuilderFn(q), ScoreFunctionBuilderFn(func))
      case _ => new FilterFunctionBuilder(ScoreFunctionBuilderFn(func))
    }
  }
}
