package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.bulk.{BulkCompatibleDefinition, BulkDefinition}
import com.sksamuel.elastic4s.http.testutils.StringExtensions.StringOps
import com.sksamuel.elastic4s.indexes.IndexDefinition
import org.scalatest.Matchers.convertToAnyShouldWrapper
import org.scalatest.{FlatSpec, FunSuite}

class BulkImplicitsTest extends FlatSpec with BulkImplicits {

  it should "build bulk definition http body" in {
    val bulkDefinition: BulkDefinition = BulkDefinition(Seq(
      IndexDefinition(IndexAndType("my_index1", "my_type1"), source = Some("""{"field1":"value1"}""")),
      IndexDefinition(IndexAndType("my_index2", "my_type2"), source = Some("""{"field2":"value2"}"""))
    ))

    val expected =
      """{"index":{"_index":"my_index1","_type":"my_type1"}}
        |{"field1":"value1"}
        |{"index":{"_index":"my_index2","_type":"my_type2"}}
        |{"field2":"value2"}
        |""".stripMargin.withUnixLineEndings

    BulkExecutable.buildBulkHttpBody(bulkDefinition) shouldBe expected
  }

}
