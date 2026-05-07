# Query DSL Reference

All query methods are available via `import com.sksamuel.elastic4s.ElasticDsl._`.
Every query type is a case class with chainable setter methods — all fields are optional unless noted.

---

## Full-text queries

### matchQuery

```scala
matchQuery(field: String, value: Any): MatchQuery
```

```scala
matchQuery("title", "elasticsearch guide")
  .operator(Operator.AND)          // AND | OR (default OR)
  .analyzer("english")
  .fuzziness("AUTO")               // "AUTO", "0", "1", "2"
  .prefixLength(2)
  .maxExpansions(50)
  .fuzzyTranspositions(true)
  .minimumShouldMatch("75%")
  .zeroTermsQuery(ZeroTermsQuery.None)   // None | All
  .autoGenerateSynonymsPhraseQuery(false)
  .lenient(true)
  .boost(1.5)
  .queryName("my_match")
```

Shortcuts:
```scala
matchQuery("body", "foo").withAndOperator()  // .operator(Operator.AND)
matchQuery("body", "foo").withOrOperator()   // .operator(Operator.OR)
```

---

### matchPhraseQuery

```scala
matchPhraseQuery(field: String, value: Any): MatchPhraseQuery
```

```scala
matchPhraseQuery("title", "quick brown fox")
  .slop(1)          // number of word transpositions allowed
  .analyzer("english")
  .boost(2.0)
```

---

### matchPhrasePrefixQuery

```scala
matchPhrasePrefixQuery(field: String, value: Any): MatchPhrasePrefixQuery
```

```scala
matchPhrasePrefixQuery("title", "quick bro")
  .maxExpansions(10)   // max number of terms to match as prefix
  .slop(0)
  .analyzer("standard")
  .boost(1.0)
```

---

### matchBoolPrefixQuery

```scala
matchBoolPrefixQuery(field: String, value: Any): MatchBoolPrefixQuery
```

Analyzes the input and constructs a `bool` query from the tokens, using `prefix` on the last term:

```scala
matchBoolPrefixQuery("title", "quick bro")
  .operator(Operator.AND)
  .minimumShouldMatch("2")
  .fuzziness("AUTO")
  .prefixLength(0)
  .maxExpansions(50)
  .boost(1.0)
```

---

### multiMatchQuery

```scala
multiMatchQuery(text: String): MultiMatchQuery
```

```scala
multiMatchQuery("elasticsearch guide")
  .fields("title", "body", "summary")          // equal weight
  .field("title", 3.0)                         // per-field boost (appends)
  .fields(Map("title" -> 2.0, "body" -> 1.0))  // map form
  .matchType(MultiMatchQueryBuilderType.BEST_FIELDS)   // default
  // Types: BEST_FIELDS | MOST_FIELDS | CROSS_FIELDS | PHRASE | PHRASE_PREFIX | BOOL_PREFIX
  .tieBreaker(0.3)      // for BEST_FIELDS: score from other fields × tieBreaker
  .operator(Operator.AND)
  .minimumShouldMatch("1")
  .fuzziness("AUTO")
  .prefixLength(0)
  .maxExpansions(50)
  .analyzer("english")
  .slop(0)              // for PHRASE / PHRASE_PREFIX
  .boost(1.0)
  .autoGenerateSynonymsPhraseQuery(true)
```

---

### queryStringQuery / query

```scala
queryStringQuery(q: String): QueryStringQuery
query(q: String): QueryStringQuery             // alias
```

Full Lucene query syntax: `title:(elasticsearch OR kibana) AND status:published`.

```scala
queryStringQuery("title:elastic* AND date:[2024-01-01 TO *]")
  .defaultField("body")
  .field("title").field("body", 2.0)
  .defaultOperator("AND")                  // "AND" | "OR"
  .analyzer("standard")
  .quoteAnalyzer("english")
  .analyzeWildcard(true)
  .allowLeadingWildcard(false)
  .lenient(true)
  .fuzziness("AUTO")
  .fuzzyPrefixLength(0)
  .fuzzyMaxExpansions(50)
  .phraseSlop(0)
  .minimumShouldMatch("1")
  .autoGenerateSynonymsPhraseQuery(true)
  .timeZone("Europe/Paris")
  .boost(1.0)
```

---

### simpleStringQuery

```scala
simpleStringQuery(q: String): SimpleStringQuery
```

Simplified query syntax, never throws a parse error. Implicit conversion: `"foo bar"` becomes a `SimpleStringQuery`.

```scala
simpleStringQuery("elasticsearch +guide -beginner")
  .field("title").field("body", 2.0)
  .defaultOperator("AND")
  .analyzer("standard")
  .analyzeWildcard(true)
  .minimumShouldMatch("75%")
  .lenient(true)
  .flags(SimpleQueryStringFlag.AND, SimpleQueryStringFlag.PHRASE, SimpleQueryStringFlag.FUZZY)
  // All flags: ALL, NONE, AND, NOT, OR, PREFIX, PHRASE, PRECEDENCE, ESCAPE, WHITESPACE, FUZZY, NEAR, SLOP
  .quoteFieldSuffix(".exact")
  .autoGenerateSynonymsPhraseQuery(false)
```

---

### combinedFieldsQuery

```scala
combinedFieldsQuery(query: String, fields: Seq[String]): CombinedFieldsQuery
```

Searches across multiple text fields as if they were one combined field (BM25F scoring):

```scala
combinedFieldsQuery("elasticsearch guide", Seq("title", "body", "summary"))
```

---

## Term-level queries

### termQuery

Exact match — no analysis. Use on `keyword`, numeric, boolean, date fields.

```scala
termQuery(field: String, value: Any): TermQuery

termQuery("status", "published")
  .boost(2.0)
  .queryName("status_filter")
```

Implicit conversion: `("status", "published")` becomes a `TermQuery`.

---

### termsQuery

```scala
termsQuery[T](field: String, values: T*): TermsQuery[T]
termsQuery[T](field: String, values: Iterable[T]): TermsQuery[T]

termsQuery("status", "published", "draft", "review")
termsQuery("id", List(1, 2, 3, 4))
```

---

### termsLookupQuery

Fetches the terms to match from another document:

```scala
termsLookupQuery(field: String, path: String, ref: DocumentRef): TermsLookupQuery

import com.sksamuel.elastic4s.requests.common.DocumentRef
termsLookupQuery("tag_ids", "tags", DocumentRef("user-tags", "user123"))
```

---

### termsSetQuery

At least N terms must match. N is specified via a field or a script:

```scala
// via field
termsSetQuery("programming_languages", Set("scala", "java", "python"), minimumShouldMatchField = "required_languages")

// via script
import com.sksamuel.elastic4s.requests.script.Script
termsSetQuery("skills", Set("scala", "zio", "cats"), minimumShouldMatchScript = Script("params.num_required_matches"))
```

---

### wildcardQuery

```scala
wildcardQuery(field: String, value: Any): WildcardQuery

wildcardQuery("name.keyword", "elas*ic*")
  .boost(1.0)
  .caseInsensitive(true)
  .rewrite("constant_score")
```

---

### prefixQuery

```scala
prefixQuery(field: String, value: Any): PrefixQuery

prefixQuery("slug", "elast")
  .boost(1.0)
  .caseInsensitive(false)
  .rewrite("constant_score")
```

---

### regexQuery

```scala
regexQuery(field: String, value: String): RegexQuery

regexQuery("code", "[A-Z]{2,3}-[0-9]+")
  .flags(RegexpFlag.INTERSECTION, RegexpFlag.COMPLEMENT)
  .maxDeterminedStates(10000)
  .caseInsensitive(false)
  .rewrite("constant_score")
  .boost(1.0)
```

---

### rangeQuery

```scala
rangeQuery(field: String): RangeQuery
```

```scala
// Numeric
rangeQuery("price").gte(10).lte(100)
rangeQuery("age").gt(18)

// Date — use Elasticsearch date math or ISO strings
rangeQuery("created_at")
  .gte("2024-01-01")
  .lt("now")
  .format("yyyy-MM-dd")
  .timeZone("Europe/Paris")

// Date math
rangeQuery("created_at").gte("now-7d/d").lt("now/d")

// Boost
rangeQuery("score").gte(80).lte(100).boost(1.5)
```

---

### existsQuery

```scala
existsQuery(field: String): ExistsQuery

existsQuery("thumbnail_url")   // matches docs where field is present and non-null
```

---

### fuzzyQuery

```scala
fuzzyQuery(field: String, value: String): FuzzyQuery

fuzzyQuery("title", "elasticserch")
  .fuzziness("AUTO")      // "AUTO", "0", "1", "2"
  .prefixLength(1)
  .maxExpansions(50)
  .transpositions(true)
  .rewrite("constant_score")
  .boost(1.0)
```

---

### idsQuery

```scala
idsQuery(ids: Any*): IdQuery
idsQuery(ids: Iterable[Any]): IdQuery

idsQuery("doc1", "doc2", "doc3")
idsQuery(List("a", "b", "c"))
```

---

## Compound queries

### boolQuery

```scala
boolQuery(): BoolQuery
```

```scala
boolQuery()
  .must(matchQuery("body", "elasticsearch"))     // scores contribute, ALL must match
  .filter(termQuery("status", "published"))      // no scoring, ALL must match
  .should(termQuery("featured", true))           // boosts score, none required by default
  .mustNot(existsQuery("deleted_at"))            // none must match
  .minimumShouldMatch(1)                         // at least N 'should' clauses must match
  .boost(1.5)
```

`must`, `should`, `not`, `filter` each **replace** the existing list.
Use `withMust`, `withShould`, `withNot` to **append** instead:

```scala
val base    = boolQuery().must(termQuery("type", "article"))
val refined = base.withMust(rangeQuery("date").gte("2024-01-01"))
```

Top-level shortcut functions (create a `boolQuery` with one clause type):

```scala
must(matchQuery("a", "x"), termQuery("b", "y"))      // boolQuery().must(...)
should(termQuery("cat", "a"), termQuery("cat", "b")) // boolQuery().should(...)
filter(termQuery("active", true))                    // boolQuery().filter(...)
not(existsQuery("deleted_at"))                       // boolQuery().not(...)
```

---

### constantScoreQuery

Wraps a filter query, assigns a constant score to all matching documents:

```scala
constantScoreQuery(query: Query): ConstantScore

constantScoreQuery(termQuery("status", "published")).boost(1.0)
```

---

### dismax

Returns the maximum score from all sub-queries, with a small contribution from the others:

```scala
dismax(first: Query, rest: Query*): DisMaxQuery
dismax(queries: Iterable[Query]): DisMaxQuery

dismax(
  matchQuery("title", "elasticsearch"),
  matchQuery("body", "elasticsearch")
).tieBreaker(0.3).boost(1.0)
```

---

### boostingQuery

Penalises documents that match the `negativeQuery`:

```scala
boostingQuery(positiveQuery: Query, negativeQuery: Query, negativeBoost: Double): BoostingQuery

boostingQuery(
  positiveQuery = matchQuery("body", "elasticsearch"),
  negativeQuery = termQuery("category", "deprecated"),
  negativeBoost = 0.2
)
```

---

### functionScoreQuery

Modifies the relevance score using scoring functions:

```scala
functionScoreQuery(): FunctionScoreQuery
functionScoreQuery(query: Query): FunctionScoreQuery
```

```scala
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer._

functionScoreQuery(matchQuery("body", "elasticsearch"))
  .functions(
    // weight: multiply the score by a constant
    WeightScore(2.0).filter(termQuery("featured", true)),

    // field value factor: use a numeric field to boost
    FieldValueFactor("popularity")
      .modifier(FieldValueFactorFunctionModifier.LOG1P)
      .factor(1.2)
      .missing(1.0),

    // random score: randomize but keep consistent per user/session
    RandomScoreFunction(seed = 42L, fieldName = "_seq_no"),

    // script score: arbitrary scoring via script
    ScriptScore(Script("_score * doc['boost_factor'].value")),

    // decay: penalise by distance from a reference point
    GaussianDecayScore(field = "date", origin = "now", scale = "10d")
      .offset("2d").decay(0.5),

    LinearDecayScore(field = "price", origin = "50", scale = "20"),
    ExponentialDecayScore(field = "distance", origin = "0", scale = "1km")
  )
  .scoreMode(FunctionScoreQueryScoreMode.Multiply) // First|Avg|Max|Min|Sum|Multiply
  .boostMode(CombineFunction.Multiply)             // Avg|Min|Max|Sum|Multiply|Replace
  .maxBoost(100.0)
  .minScore(1.0)
  .boost(1.0)
```

Each `ScoreFunction` accepts an optional `.filter(query)` to apply it only to matching documents.

---

## Geo queries

### geoDistanceQuery

```scala
geoDistanceQuery(field: String, lat: Double, long: Double): GeoDistanceQuery
geoDistanceQuery(field: String, geohash: String): GeoDistanceQuery
```

```scala
import com.sksamuel.elastic4s.requests.common.DistanceUnit

geoDistanceQuery("location", 48.8566, 2.3522)
  .distance("10km")                   // string form
  .distance(10.0, DistanceUnit.KM)    // typed form
  .geoDistance(GeoDistance.Arc)       // Arc | Plane
  .validationMethod(GeoValidationMethod.STRICT)
  .ignoreUnmapped(false)
  .boost(1.0)
```

---

### geoBoxQuery (geo_bounding_box)

```scala
geoBoxQuery(field: String): GeoBoundingBoxQuery
geoBoxQuery(field: String, topleft: String, bottomright: String): GeoBoundingBoxQuery
```

```scala
import com.sksamuel.elastic4s.requests.searches.GeoPoint

geoBoxQuery("location")
  .withCorners(topLeft = GeoPoint(50.0, -1.0), bottomRight = GeoPoint(48.0, 2.0))

// via geohash strings
geoBoxQuery("location").geohash("u0yw", "u0y")

// OGC form (bottomLeft, topRight)
geoBoxQuery("location").withCornersOGC(GeoPoint(48.0, -1.0), GeoPoint(50.0, 2.0))
```

---

### geoPolygonQuery

```scala
geoPolygonQuery(field: String).points(GeoPoint*): GeoPolygonQuery
geoPolygonQuery(field: String, points: Iterable[GeoPoint]): GeoPolygonQuery
```

```scala
import com.sksamuel.elastic4s.requests.searches.GeoPoint

geoPolygonQuery("location").points(
  GeoPoint(50.0, -1.0),
  GeoPoint(50.0,  2.0),
  GeoPoint(48.0,  2.0),
  GeoPoint(48.0, -1.0)
)
```

---

### geoShapeQuery

```scala
geoShapeQuery(field: String, shape: Shape): GeoShapeQuery

import com.sksamuel.elastic4s.requests.searches.queries.geo._

geoShapeQuery("location", CircleShape(GeoPoint(48.8566, 2.3522), "10km"))
geoShapeQuery("location", EnvelopeShape(GeoPoint(50.0, -1.0), GeoPoint(48.0, 2.0)))
```

---

## Joining queries

### nestedQuery

```scala
nestedQuery(path: String, query: Query): NestedQuery

nestedQuery("comments", matchQuery("comments.body", "great"))
  .scoreMode(ScoreMode.Avg)       // Avg | Max | Min | None | Sum
  .ignoreUnmapped(false)
  .inner(innerHits("top_comments").size(3).sortBy(fieldSort("comments.date").desc()))
  .boost(1.0)
```

`innerHits` builder:

```scala
innerHits(name: String): InnerHit

innerHits("top_comments")
  .size(5)
  .from(0)
  .sortBy(fieldSort("date").desc())
  .storedFieldNames(Seq("author"))
  .docValueFields(Seq("score"))
  .trackScores(true)
  .explain(true)
  .version(true)
```

---

### hasChildQuery

```scala
hasChildQuery(childType: String, query: Query, scoreMode: ScoreMode): HasChildQuery

hasChildQuery("comment", matchQuery("body", "great"), ScoreMode.Sum)
  .minChildren(1)
  .maxChildren(100)
  .ignoreUnmapped(false)
  .innerHit(innerHits("matching_comments").size(3))
  .boost(1.0)
```

---

### hasParentQuery

```scala
hasParentQuery(parentType: String, query: Query, score: Boolean): HasParentQuery

hasParentQuery("blog", termQuery("category", "scala"), score = true)
  .ignoreUnmapped(false)
  .innerHit(innerHits("parent").size(1))
  .boost(1.0)
```

---

## Specialized queries

### moreLikeThisQuery

```scala
moreLikeThisQuery(fields: String*): MoreLikeThisExpectsLikes
```

```scala
// like text
moreLikeThisQuery("title", "body")
  .likeTexts("Elasticsearch is great for search")
  .minTermFreq(1)
  .minDocFreq(1)
  .maxQueryTerms(25)
  .minWordLength(3)
  .stopWords("the", "a", "an")
  .minShouldMatch("30%")
  .boost(1.0)
  .boostTerms(0.0)
  .include(false)

// like documents
import com.sksamuel.elastic4s.requests.common.DocumentRef
moreLikeThisQuery("title", "body")
  .likeDocs(DocumentRef("articles", "doc1"), DocumentRef("articles", "doc2"))

// negative examples
moreLikeThisQuery("body").likeTexts("good content").unlikeText("bad content")
```

---

### percolateQuery

Tests stored queries against a document:

```scala
percolateQuery("query").usingId(index = "my-index", id = "doc1")
percolateQuery("query").usingSource("""{"title":"elasticsearch"}""")
percolateQuery("query").usingSource(myObj)   // requires implicit Indexable[T]
```

---

### pinnedQuery

Promotes specific document IDs to the top of results:

```scala
pinnedQuery(ids = List("featured-1", "featured-2"), organic = matchQuery("body", "elasticsearch"))
```

---

### scriptQuery

```scala
import com.sksamuel.elastic4s.requests.script.Script

scriptQuery(Script("doc['price'].value * 2 > params.threshold").params(Map("threshold" -> 100)))
scriptQuery("doc['price'].value > 100")   // string shorthand
```

---

### scriptScoreQuery

Replaces the document score with a script result:

```scala
scriptScoreQuery()
  .query(matchQuery("body", "elasticsearch"))
  .script(Script("_score + doc['boost_factor'].value"))
  .minScore(0.5)
  .boost(1.0)
```

---

### rankFeatureQuery

Boosts based on a `rank_feature` or `rank_features` field:

```scala
rankFeatureQuery("popularity")               // saturation (default)
rankFeatureQuery("pagerank").saturation()
rankFeatureQuery("pagerank").saturation(pivot = 8.0)
rankFeatureQuery("pagerank").log(scalingFactor = 4.0)
rankFeatureQuery("pagerank").sigmoid(pivot = 7.0, exponent = 0.6)
rankFeatureQuery("pagerank").linear()
```

---

### distanceFeatureQuery

Boosts documents closer to a reference date or geo-point:

```scala
distanceFeatureQuery(field = "created_at", origin = "now",             pivot = "7d")
distanceFeatureQuery(field = "location",   origin = "48.8566,2.3522",  pivot = "1km")
```

---

### semanticQuery

ELSER / semantic search on a `semantic_text` field:

```scala
semanticQuery(field = "body_semantic", query = "what is the meaning of life")
```

---

### sparseVectorQuery

```scala
// with inference (ELSER model)
sparseVectorQuery("ml.tokens", inferenceId = "my-elser-model", query = "search query")

// with pre-computed token weights
sparseVectorQuery("ml.tokens", queryVector = Map("elasticsearch" -> 0.87, "search" -> 0.65))
```

---

### rawQuery

Escape hatch — pass raw Elasticsearch JSON directly:

```scala
rawQuery("""{"term":{"status":{"value":"published"}}}""")
```

Useful for query types not yet implemented in the DSL.

---

## Span queries (reference)

All span queries implement `SpanQuery` and can be nested:

```scala
spanTermQuery(field, value)
spanNearQuery(Seq(spanTermQuery("f","a"), spanTermQuery("f","b")), slop = 0)
spanOrQuery(spanTermQuery("f","a"), spanTermQuery("f","b"))
spanNotQuery(include = spanTermQuery("f","a"), exclude = spanTermQuery("f","b"))
spanFirstQuery(query = spanTermQuery("f","a"), end = 3)
spanContainingQuery(big = spanNearQuery(...), little = spanTermQuery(...))
spanWithinQuery(big = spanNearQuery(...), little = spanTermQuery(...))
spanMultiTermQuery(wildcardQuery("field", "el*"))
spanFieldMaskingQuery(fieldToMask = "text", query = spanTermQuery("text.english", "fox"))
```

---

## Common options (available on most queries)

| Method | Type | Description |
|--------|------|-------------|
| `.boost(n)` | `Double` | Multiply this query's contribution to the score |
| `.queryName(name)` | `String` | Tag the query — appears in `matched_queries` in the hit response |
