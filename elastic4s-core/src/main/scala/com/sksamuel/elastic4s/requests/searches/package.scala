package com.sksamuel.elastic4s.requests

import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation

package object searches {
  def defaultCustomAggregationHandler: PartialFunction[AbstractAggregation, XContentBuilder] = {
    case ni =>
      throw new NotImplementedError(
        s"Aggregation ${ni.getClass.getName} has not yet been implemented. Please add a PR!"
      )
  }
}
