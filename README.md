elastic4s
=========

Elastic4s is a Scala DSL for ElasticSearch. This gives you the full power of a type safe DSL to construct your queries, indexing, etc.

Currently elastic4s does not cover all the functionality of the java client, but it covers enough for most use cases. We are continually adding more coverage too - why not fork and contribute?

[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)


## Quick Examples

#### DSL Syntax

| Operation | General Syntax |
|-----------|----------------|
| Index | ```index into <index/type> fields { <fieldblock> } [routing <routing> version <version> parent <parent>.....]``` |
| Get | ```get id <id> from <index/type>``` |
| Delete |  To delete by id ```delete id <id> in <index/type> [routing <routing> version <version> parent <parent>.....]```
            To delete by query ```delete query { <queryblock> } [routing <routing> version <version> parent <parent>.....]```
| Search | To search ```search in <index/type> query { <queryblock> } filter { <filterblock> } sort { <sortblock> } ....``` |

#### Create Index Example

To create an index you need to import the CreateIndexDsl object. Then you are able to use the dsl to build create-index requests like:

```scala
class CreateIndexReqExample extends CreateIndexDsl {

        import CreateIndexDsl._
        val req = create index "users" shards 3 replicas 4 mappings {

            "tweets" source true as {

                id fieldType StringType analyzer KeywordAnalyzer store true and
                  "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "content" fieldType StringType analyzer StopAnalyzer

            } and "users" source false as {

                "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "location" fieldType GeoPointType
            }
        }
}
```

#### Index Example

To index you need to import the IndexDsl object. Then you are able to use the dsl to build index requests like :

```scala
class IndexReqExample {

      import IndexDsl._
      val req = index into "twitter/tweet" id 9999 fields (
          "user" -> "sammy",
          "post_date" -> "2011-11-15T14:12:12",
          "message" -> "I have an ID"
          ) routing "users" ttl 100000
}
```