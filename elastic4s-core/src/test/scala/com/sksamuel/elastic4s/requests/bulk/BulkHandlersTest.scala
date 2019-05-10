package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.ElasticRequest
import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.requests.indexes.IndexRequest
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class BulkHandlersTest extends FlatSpec with BulkHandlers {

  import com.sksamuel.elastic4s.testutils.StringExtensions._

  it should "build bulk definition http body" in {
    val request: BulkRequest = BulkRequest(Seq(
      IndexRequest("my_index1", source = Some("""{"field1":"value1"}""")),
      IndexRequest("my_index2", source = Some("""{"field2":"value2"}"""))
    ))

    val expected =
      """{"index":{"_index":"my_index1"}}
        |{"field1":"value1"}
        |{"index":{"_index":"my_index2"}}
        |{"field2":"value2"}
        |""".stripMargin.withUnixLineEndings

    val esRequest: ElasticRequest = BulkHandler.build(request)
    esRequest.entity should not be empty
    esRequest.entity.get shouldBe a[StringEntity]
    esRequest.entity.get.get shouldBe expected
  }
}
