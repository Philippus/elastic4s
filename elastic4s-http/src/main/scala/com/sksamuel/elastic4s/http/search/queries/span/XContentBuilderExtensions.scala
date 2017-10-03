package com.sksamuel.elastic4s.http.search.queries.span

import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.XContentBuilder

object XContentBuilderExtensions {
  private val CommaByte: Byte = ','

  implicit class RichXContentBuilder(builder: XContentBuilder) {
    /**
      * Default XContentFactory.rawValue method does not add comma symbol between array elements.
      * This is low level workaround method to insert comma byte between array elements bytes.
      *
      * NOTE: we can't do builder.rawValue(SINGLE_COMMA_BYTE) so we first need to build entire array object in memory.
      * NOTE: intentionally using low level methods to obtain max speed.
      *
      * @return original builder
      */
    def rawArrayValue(arrayObjects: Seq[XContentBuilder]): XContentBuilder = {
      val elementsBytes: List[Array[Byte]] = arrayObjects.map(_.bytes.toBytesRef.bytes).toList

      elementsBytes match {
        case Nil =>
        case head :: tail =>
          val resultBytes = allocateResultByteArray(elementsBytes)
          var currentDistPos = copyInternal(head, resultBytes, 0)
          tail.foreach(bytes => {
            resultBytes(currentDistPos) = CommaByte
            currentDistPos += 1
            currentDistPos += copyInternal(bytes, resultBytes, currentDistPos)
          })
          builder.rawValue(new BytesArray(resultBytes))
      }

      builder
    }
  }

  private def copyInternal(src: Array[Byte], dist: Array[Byte], distPos: Int): Int = {
    val srcLength = src.length
    System.arraycopy(src, 0, dist, distPos, srcLength)
    srcLength
  }

  private def allocateResultByteArray(elementsBytes: Seq[Array[Byte]]) = {
    val commasCount = elementsBytes.size - 1
    val totalBufferSize = elementsBytes.map(_.length).sum + commasCount
    new Array[Byte](totalBufferSize)
  }
}

