package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.SimpleFieldValue
import com.sksamuel.elastic4s.json.{XContentFactory, XContentFieldValueWriter}

object AnalyseRequestContentBuilder {

  def apply(request: AnalyzeRequest): String = {

    val source = XContentFactory.jsonBuilder()

    def simpleFieldValue(name:String,value:String) = {
      XContentFieldValueWriter(source,SimpleFieldValue(name,value))
    }

    request.text.foreach(XContentFieldValueWriter(source, _))
    source.endObject().string()
  }

}
