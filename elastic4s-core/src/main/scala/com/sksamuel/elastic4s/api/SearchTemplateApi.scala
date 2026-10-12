package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.TemplateSearchRequest

trait SearchTemplateApi {
  def templateSearch(indexes: Indexes): TemplateSearchExpectsName = new TemplateSearchExpectsName(indexes)

  class TemplateSearchExpectsName(indexes: Indexes) {
    def name(name: String): TemplateSearchExpectsParams = new TemplateSearchExpectsParams(indexes, name)

    class TemplateSearchExpectsParams(indexes: Indexes, name: String) {
      def params(params: Map[String, Any]): TemplateSearchRequest =
        TemplateSearchRequest(indexes, name, params)
    }
  }
}
