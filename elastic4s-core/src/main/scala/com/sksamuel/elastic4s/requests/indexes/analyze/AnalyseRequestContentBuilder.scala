package com.sksamuel.elastic4s.requests.indexes.analyze

import com.sksamuel.elastic4s.SimpleFieldValue
import com.sksamuel.elastic4s.json.{XContentFactory, XContentFieldValueWriter}

object AnalyseRequestContentBuilder {

  def apply(request: AnalyzeRequest): String = {

    val source = XContentFactory.jsonBuilder()

    def simpleFieldValue(name:String,value:Any): Unit = {
      XContentFieldValueWriter(source,SimpleFieldValue(name,value))
    }

    simpleFieldValue("text",request.text)
    request.analyzer.foreach(simpleFieldValue("analyzer",_))

    if(request.explain) {
      simpleFieldValue("explain",true)
    }

    source.endObject().string()
  }

}
