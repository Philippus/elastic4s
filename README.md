elastic4s
=========

Elastic4s is a Scala DSL for ElasticSearch. This gives you the full power of a type safe DSL to construct your queries, indexing, etc.

Currently elastic4s does not cover all the functionality of the java client, but it covers enough for most use cases. We are continually adding more coverage too - why not fork and contribute?

[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)

### Create Index Example

To create an index you need to mixin the CreateIndexDsl trait. Then you are able to use the dsl to create create-index requests as such

```scala
class CreateIndexReqExample extends CreateIndexDsl {

    val req = createIndex("users") {
        shards(5) // optional of course
        replicas(2) // optional of course
        mappings { // all mappings are optional as elastic will create dynamically
            mapping("users") {
                id.fieldType(StringType).analyzer(KeywordAnalyzer).store
                field("name").fieldType(StringType).analyzer(WhitespaceAnalyzer)
            }
            mapping("tweets") {
                source(true)
            }
            mapping("locations") {
                id.fieldType(StringType).analyzer(KeywordAnalyzer).store
                field("name").fieldType(StringType).analyzer(WhitespaceAnalyzer)
            }
        }
    }
}
```

### Index Example

To index you need to mixin the IndexDsl trait. Then you are able to use the dsl to create index requests as such

```scala
class IndexReqExample extends IndexDsl {

    val req = index("twitter", "tweets") {

        routing("kusers")
        version(4)
        timestamp("2009-11-15T14:12:12")

        "user" -> "sammy"
        "post_date" -> "2009-11-15T14:12:12"
        "message" -> "trying out Elastic Search Scala DSL"
    }
}
```