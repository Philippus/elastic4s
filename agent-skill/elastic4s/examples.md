# Complete Examples

All examples assume:

```scala
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties, RequestSuccess, RequestFailure}
import com.sksamuel.elastic4s.http.JavaClient
import cats.implicits.catsStdInstancesForFuture
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
```

---

## 1. Client setup

```scala
val client = ElasticClient(JavaClient(ElasticProperties("http://localhost:9200")))

// With authentication:
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions}
implicit val opts: CommonRequestOptions = CommonRequestOptions.defaults.copy(
  authentication = Some(Authentication.UsernamePassword("elastic", "changeme"))
)

// Always close when done:
client.close()
```

---

## 2. Simple search

```scala
val future: Future[_] = client.execute {
  search("articles")
    .query(matchQuery("title", "elasticsearch"))
    .size(10)
}.map {
  case RequestSuccess(_, _, _, result) =>
    println(s"${result.totalHits} hits")
    result.hits.hits.foreach(h => println(h.sourceAsString))
  case RequestFailure(_, _, _, error) =>
    println(s"Error: ${error.reason}")
}
```

---

## 3. Bool query composition

```scala
client.execute {
  search("products")
    .query(
      boolQuery()
        .must(
          matchQuery("description", "wireless headphones")
        )
        .filter(
          termQuery("status", "in_stock"),
          rangeQuery("price").gte(20).lte(300)
        )
        .should(
          termQuery("brand", "sony"),
          termQuery("brand", "bose")
        )
        .minimumShouldMatch(1)
        .mustNot(
          existsQuery("discontinued_at")
        )
    )
    .sortBy(fieldSort("price").asc())
    .size(20)
}
```

---

## 4. Term-level queries

```scala
client.execute {
  search("orders")
    .query(
      boolQuery()
        .filter(
          termQuery("status", "shipped"),
          termsQuery("region", "eu-west", "eu-central"),
          rangeQuery("created_at")
            .gte("now-30d/d")
            .lte("now/d")
            .format("epoch_millis||yyyy-MM-dd"),
          existsQuery("tracking_number")
        )
        .mustNot(
          termQuery("cancelled", true)
        )
    )
}
```

---

## 5. Nested query with inner hits

```scala
client.execute {
  search("blogs")
    .query(
      nestedQuery(
        path   = "comments",
        query  = boolQuery()
          .must(matchQuery("comments.body", "great article"))
          .filter(rangeQuery("comments.rating").gte(4))
      )
      .scoreMode("avg")
      .inner(
        innerHits("matching_comments")
          .size(3)
          .sortBy(fieldSort("comments.rating").desc())
          .fetchSource(true)
      )
    )
    .size(10)
}.map {
  case RequestSuccess(_, _, _, result) =>
    result.hits.hits.foreach { hit =>
      println(s"Post: ${hit.id}")
      hit.innerHits("matching_comments").hits.foreach { inner =>
        println(s"  Comment rating: ${inner.source("rating")}")
      }
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 6. Function score query

```scala
client.execute {
  search("products")
    .query(
      functionScoreQuery(matchQuery("name", "laptop"))
        .functions(
          fieldValueFactorScore("popularity")
            .factor(1.5)
            .modifier(FieldValueFactorFunctionModifier.LOG1P)
            .missing(1.0),
          weightScore(2.0).filter(termQuery("featured", true))
        )
        .scoreMode(FunctionScoreQueryScoreMode.Sum)
        .boostMode(CombineFunction.Multiply)
        .maxBoost(3.0)
    )
    .sortBy(scoreSort())
}
```

---

## 7. From / size pagination

```scala
def searchPage(page: Int, pageSize: Int = 20) =
  client.execute {
    search("articles")
      .query(matchQuery("body", "scala"))
      .from(page * pageSize)
      .size(pageSize)
      .sortBy(fieldSort("published_at").desc())
  }.map(_.result)

// Page 0 then page 1:
for {
  p0 <- searchPage(0)
  p1 <- searchPage(1)
} yield {
  println(s"Total: ${p0.totalHits}")
  p0.hits.hits.foreach(h => println(h.id))
}
```

---

## 8. Search after (deep pagination)

```scala
import scala.concurrent.duration._

// First page
val resp1 = client.execute {
  search("events")
    .query(matchAllQuery())
    .sortBy(
      fieldSort("timestamp").desc(),
      fieldSort("_id").asc()   // unique tiebreaker
    )
    .size(100)
}.await.result

// Subsequent pages
def nextPage(sortValues: Seq[AnyRef]) =
  client.execute {
    search("events")
      .query(matchAllQuery())
      .sortBy(
        fieldSort("timestamp").desc(),
        fieldSort("_id").asc()
      )
      .size(100)
      .searchAfter(sortValues)
  }.map(_.result)

// Iterate:
var current = resp1
while (current.hits.nonEmpty) {
  current.hits.hits.foreach(processHit)
  val sortValues = current.hits.hits.last.sort.get
  current = nextPage(sortValues).await
}
```

---

## 9. Scroll with SearchIterator

```scala
import com.sksamuel.elastic4s.requests.searches.SearchIterator
import scala.concurrent.duration._

implicit val timeout: FiniteDuration = 30.seconds

val iterator: Iterator[SearchHit] = SearchIterator.hits(
  client,
  search("logs")
    .query(rangeQuery("@timestamp").gte("now-1d"))
    .sortBy(fieldSort("@timestamp").asc())
    .size(500)
    .scroll("2m")
)

iterator.foreach { hit =>
  println(hit.sourceField("message"))
}
// Scroll is automatically closed when iterator is exhausted
```

---

## 10. Terms aggregation with sub-aggregation

```scala
client.execute {
  search("orders")
    .query(termQuery("status", "completed"))
    .size(0)   // don't return hits
    .aggs(
      termsAgg("by_country", "country")
        .size(20)
        .order(TermsOrder("total_revenue", asc = false))
        .subAggregations(
          sumAgg("total_revenue", "amount"),
          avgAgg("avg_order", "amount"),
          cardinalityAgg("unique_customers", "customer_id")
        )
    )
}.map {
  case RequestSuccess(_, _, _, result) =>
    import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.Terms
    val byCountry = result.aggs.result[Terms]("by_country")
    byCountry.buckets.foreach { bucket =>
      val rev = bucket.sum("total_revenue").value
      val avg = bucket.avg("avg_order").value
      val uniq = bucket.cardinality("unique_customers").value
      println(s"${bucket.key}: revenue=$rev avg=$avg customers=$uniq")
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 11. Date histogram with post filter

```scala
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateHistogram

client.execute {
  search("sales")
    .query(matchAllQuery())
    .aggs(
      // Aggregation runs over ALL docs (before postFilter)
      dateHistogramAgg("monthly_revenue", "sale_date")
        .calendarInterval(DateHistogramInterval.Month)
        .format("yyyy-MM")
        .subAggregations(sumAgg("revenue", "amount"))
    )
    // postFilter only affects returned hits, not aggregations
    .postFilter(termQuery("region", "emea"))
    .size(0)
}.map {
  case RequestSuccess(_, _, _, result) =>
    val hist = result.aggs.result[DateHistogram]("monthly_revenue")
    hist.buckets.foreach { b =>
      println(s"${b.date}: ${b.sum("revenue").value}")
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 12. Composite aggregation (paginate aggregations)

```scala
import com.sksamuel.elastic4s.requests.searches.aggs.{
  CompositeAggregation, TermsValueSource, DateHistogramValueSource
}
import com.sksamuel.elastic4s.requests.searches.aggs.CompositeAggregation._

def compositeSearch(after: Option[Map[String, Any]] = None) = {
  val agg = CompositeAggregation(
    "by_brand_month",
    sources = Seq(
      TermsValueSource("brand", field = Some("brand")),
      DateHistogramValueSource("month",
        calendarInterval = Some("month"),
        field            = Some("sale_date"),
        format           = Some("yyyy-MM")
      )
    ),
    size  = Some(500),
    after = after
  )
  client.execute(search("sales").size(0).aggs(agg)).map(_.result)
}

// First page
var result = compositeSearch().await
while (result.aggs.compositeAgg("by_brand_month").buckets.nonEmpty) {
  val page = result.aggs.compositeAgg("by_brand_month")
  page.buckets.foreach { b =>
    println(s"${b.key}: ${b.docCount}")
  }
  page.afterKey match {
    case Some(ak) => result = compositeSearch(Some(ak)).await
    case None     => return
  }
}
```

---

## 13. Type-safe result mapping with HitReader

**With Jackson (automatic):**

```scala
// SBT: "com.sksamuel.elastic4s" %% "elastic4s-json-jackson" % version
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

case class Article(id: String, title: String, price: Double, tags: List[String])

client.execute {
  search("articles").query(matchAllQuery()).size(50)
}.map {
  case RequestSuccess(_, _, _, result) =>
    val articles: IndexedSeq[Article]      = result.to[Article]
    val safe:     IndexedSeq[Try[Article]] = result.safeTo[Article]
    articles.foreach(a => println(a.title))
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

**With Circe (automatic):**

```scala
// SBT: "com.sksamuel.elastic4s" %% "elastic4s-json-circe" % version
import io.circe.generic.auto._
import com.sksamuel.elastic4s.circe._

val articles: IndexedSeq[Article] = result.to[Article]
```

**Manual implementation:**

```scala
import com.sksamuel.elastic4s.{Hit, HitReader}
import scala.util.Try

implicit val reader: HitReader[Article] = (hit: Hit) =>
  Try {
    Article(
      id    = hit.id,
      title = hit.sourceField("title").toString,
      price = hit.sourceField("price").toString.toDouble,
      tags  = hit.sourceFieldOpt("tags")
               .map(_.asInstanceOf[java.util.List[String]].asScala.toList)
               .getOrElse(Nil)
    )
  }
```

---

## 14. Source filtering and stored fields

```scala
client.execute {
  search("products")
    .query(matchAllQuery())
    // Include only certain _source fields:
    .sourceInclude("title", "price", "category")
    .sourceExclude("internal_*", "raw_html")
    // Or disable _source entirely and use stored fields:
    // .fetchSource(false)
    // .storedFields("title", "price")
    .size(20)
}.map {
  case RequestSuccess(_, _, _, result) =>
    result.hits.hits.foreach { hit =>
      // _source is filtered — only included fields present
      println(hit.sourceAsMap)

      // If storedFields were used instead:
      // hit.storedField("title").value
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 15. Highlighting

```scala
client.execute {
  search("articles")
    .query(matchQuery("body", "elasticsearch performance"))
    .highlighting(
      highlight("title")
        .numberOfFragments(0)        // return full field when no fragmentation needed
        .preTags("<mark>")
        .postTags("</mark>"),
      highlight("body")
        .fragmentSize(200)
        .numberOfFragments(3)
        .highlighterType("unified")
        .requireFieldMatch(false)
    )
    .size(10)
}.map {
  case RequestSuccess(_, _, _, result) =>
    result.hits.hits.foreach { hit =>
      val titleFrags = hit.highlightFragments("title")
      val bodyFrags  = hit.highlightFragments("body")
      println(titleFrags.headOption.getOrElse(hit.sourceField("title")))
      bodyFrags.foreach(println)
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 16. Script fields

```scala
import com.sksamuel.elastic4s.requests.script.Script

client.execute {
  search("products")
    .query(matchAllQuery())
    .scriptfields(
      scriptField("price_with_tax",
        Script("doc['price'].value * (1 + params.tax_rate)")
          .params(Map("tax_rate" -> 0.2))
      ),
      scriptField("days_since_launch",
        Script("(System.currentTimeMillis() - doc['launch_date'].value.millis) / 86400000")
      )
    )
    .sourceInclude("name", "price")
    .size(10)
}.map {
  case RequestSuccess(_, _, _, result) =>
    result.hits.hits.foreach { hit =>
      val name           = hit.sourceField("name")
      val priceWithTax   = hit.sourceField("price_with_tax")
      val daysSinceLaunch = hit.sourceField("days_since_launch")
      println(s"$name: $priceWithTax (${daysSinceLaunch}d old)")
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 17. Multi-search

```scala
client.execute {
  multi(
    search("products")
      .query(termQuery("category", "electronics"))
      .size(5)
      .sortBy(fieldSort("price").asc()),
    search("products")
      .query(termQuery("category", "clothing"))
      .aggs(avgAgg("avg_price", "price"))
      .size(0),
    search("users")
      .query(matchQuery("bio", "scala developer"))
      .size(10)
  )
}.map {
  case RequestSuccess(_, _, _, result) =>
    result.items.zipWithIndex.foreach {
      case (Right(r), i) => println(s"Query $i: ${r.totalHits} hits")
      case (Left(e), i)  => println(s"Query $i failed: ${e.error.reason}")
    }
  case RequestFailure(_, _, _, error) => println(error.reason)
}
```

---

## 18. KNN vector search

```scala
import com.sksamuel.elastic4s.requests.searches.knn.{Knn, QueryVectorBuilder}

// Exact vector
client.execute {
  search("products")
    .knn(
      Knn("description_embedding")
        .queryVector(Seq(0.1f, 0.25f, -0.3f, 0.8f))
        .k(10)
        .numCandidates(100)
        .filter(termQuery("status", "active"))
        .boost(1.5f)
    )
    .size(10)
}

// Via model inference (ELSER / e5)
client.execute {
  search("products")
    .knn(
      Knn("description_embedding")
        .queryVectorBuilder(
          QueryVectorBuilder(
            modelId   = ".multilingual-e5-small",
            modelText = "wireless noise-cancelling headphones"
          )
        )
        .k(10)
        .numCandidates(100)
    )
    .size(10)
}
```
