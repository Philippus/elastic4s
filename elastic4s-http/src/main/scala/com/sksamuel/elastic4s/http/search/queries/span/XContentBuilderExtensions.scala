package com.sksamuel.elastic4s.http.search.queries.span

import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentType}

object XContentBuilderExtensions {

  implicit class RichXContentBuilder(builder: XContentBuilder) {
    def addArray(arrayField: String, elements: Seq[XContentBuilder]): XContentBuilder = {
      builder.startArray(arrayField)
      val elementsRawString = elements.map(_.string).mkString(",")
      builder.rawValue(new BytesArray(elementsRawString), XContentType.JSON)
      builder.endArray()
    }
  }

}

