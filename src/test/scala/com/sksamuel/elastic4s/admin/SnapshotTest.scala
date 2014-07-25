package com.sksamuel.elastic4s
package admin

import org.elasticsearch.common.Priority
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

class SnapshotTest extends FlatSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  client.sync.execute(bulk(
    index into "waterways/rivers" id 11 fields(
      "name" -> "River Lune",
      "country" -> "England"
      ),
    index into "waterways/rivers" id 12 fields(
      "name" -> "River Dee",
      "country" -> "England"
      ),
    index into "waterways/rivers" id 21 fields(
      "name" -> "River Dee",
      "country" -> "Wales"
      )
  )
  )

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("waterways")
  blockUntilCount(3, "waterways")

  "snapshot" should "created and restored" in {

  }
}
