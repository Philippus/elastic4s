//

//

//

//

//

//

//

//

//  }
//
//  "scripted aggregation" - {
//    "should  compute a word count on field name" in {
//      val resp = client.execute {
//        search in "aggregations/breakingbad" aggregations {
//          aggregation.scriptedMetric("agg1")
//            .initScript("_agg['wordCount'] = []")
//            .mapScript("_agg.wordCount.add(doc['name'].values.size())")
//            .combineScript("wc = 0; for(c in _agg.wordCount) { wc += c }; return wc")
//            .reduceScript("wc = 0; for(a in _aggs) { wc += a }; return wc")
//        }
//      }.await
//      val agg = resp.aggregations.get[InternalScriptedMetric]("agg1")
//      agg.aggregation().asInstanceOf[Integer] shouldBe 21
//    }
//  }
//}
