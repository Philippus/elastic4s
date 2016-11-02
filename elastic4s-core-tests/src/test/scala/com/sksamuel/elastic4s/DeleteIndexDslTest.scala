package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class DeleteIndexDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  // hack to expose private indices method
  private val indicesMethod = classOf[DeleteIndexRequest].getDeclaredMethod("indices")
  indicesMethod.setAccessible(true)

  "a delete index request" should "accept var args" in {
    val req = delete index ("index1", "index2")
    assert(extractIndices(req.build) === Array("index1", "index2"))
  }

  it should "accept single index as a postfix" in {
    val req = delete index "places"
    assert(extractIndices(req.build) === Array("places"))
  }

  private def extractIndices(request: DeleteIndexRequest) = {
    indicesMethod.invoke(request)
  }
}
