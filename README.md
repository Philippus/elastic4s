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
### DSL Completeness

As it stands the Scala DSL covers all of the common operations - index, create, delete, delete by query, search, validate, percolate, update, explain, get, and bulk operations. There is good support for the various settings for each of these - more so than the Java client provides in the sense that more settings are provided in a type safe manner. 

However there are settings and operations (mostly admin / cluster related) that the DSL does not yet cover (pull requests welcome!). In these cases it is necessary to drop back to the Java API. This can be done by calling .java on the client object to get the underlying java elastic client, or .admin to get the admin based client, eg, the following request is a Java API request.

```
client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases where the Scala DSL has no coverage.

### Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s