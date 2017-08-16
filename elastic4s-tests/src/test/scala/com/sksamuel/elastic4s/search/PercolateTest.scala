//package com.sksamuel.elastic4s.search
//
//import com.sksamuel.elastic4s.DocumentRef
//import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
//import org.scalatest.mockito.MockitoSugar
//import org.scalatest.{FlatSpec, Matchers}
//
//class PercolateTest extends FlatSpec with Matchers with MockitoSugar with ElasticSugar with ClassloaderLocalNodeProvider {
//
//  client.execute {
//    createIndex("percolate").mappings(
//      // the first mapping is the place that holds the doc we are percolating against
//      // it is a kind of temporary type
//      mapping("doc") as {
//        textField("flavour")
//      },
//      // the second mapping is what holds our queries themselves
//      mapping("queries") as {
//        percolatorField("query")
//      }
//    )
//  }.await
//
//  // register some queries into the percolator field
//  val resp = client.execute {
//    bulk(
//      // if we don't specify the field that holds the query document, it will default to "query"
//      register(termQuery("flavour", "assam")).into("percolate" / "queries").withId(1),
//      register(matchQuery("flavour", "earl grey")).into("percolate" / "queries", "query").withId(2),
//      register(termQuery("flavour", "darjeeling")).into("percolate" / "queries", "query").withId(3)
//    )
//  }.await
//
//  blockUntilCount(3, "percolate" / "queries")
//
//  "a percolate request" should "return queries that match the document" in {
//
//    val matches = client.execute {
//      search("percolate").query(percolateQuery("doc", "query").usingSource("""{"flavour" : "assam"}"""))
//    }.await
//
//    matches.size shouldBe 1
//    matches.hits.head.id shouldBe "1"
//  }
//
//  "a percolate request for existing document" should "return queries that match the document" in {
//    // this is the document we will percolate with
//    client.execute {
//      indexInto("percolate/teas") fields "flavour" -> "darjeeling" withId "4"
//    }.await
//
//    blockUntilCount(4, "percolate")
//
//    val matches = client.execute {
//      // if we don't specify the field that holds the query document, it will default to "query"
//      search("percolate").query(percolateQuery("doc").usingId(DocumentRef("percolate", "teas", "4")))
//    }.await
//
//    matches.size shouldBe 1
//    matches.hits.head.id shouldBe "3"
//  }
//}
