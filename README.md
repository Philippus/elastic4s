elastic4s
=========

Elastic4s is a concise, idiomatic, type safe Scala DSL for ElasticSearch. This gives you the full power of a DSL to construct your queries, indexes, etc without needing to generate JSON. Sometimes you have data in JSON and its easy to use that straight in requests. Other times you want to create your requests programatically and a DSL is more convenient. Elastic4s is designed for that latter scenario.



[![Build Status](https://travis-ci.org/sksamuel/elastic4s.png)](https://travis-ci.org/sksamuel/elastic4s)


## Introduction to the DSL

The basic format of the DSL is to create requests (eg a search request or delete request) and pass them in to the execute methods on the client, which returns a response object. 

All requests on the standard client are asynchronous. These methods return a standard Scala 2.10 Future object. Eg, a search request will return a Future[SearchResponse]. The response objects are the same as for the Java API due to the fact they already are concise and convenient in Scala code.

#### DSL Syntax

| Operation | Samuel Normal Form Syntax |
|-----------|----------------|
| Create Index | ```create index <name> mappings { mappings block> } [optional settings]```|
| Index | ```index into <index/type> fields { <fieldblock> } [optional settings]``` |
| Get | ```get id <id> from <index/type> [optional settings]```|
| Delete by id |  ```delete id <id> in <index/type> [optional settings]```
| Delete by query |```delete from <index/type> query { <queryblock> } [optional settings]```
| Search | ```search in <index/type> query { <queryblock> } filter { <filterblock> } facets { <facetblock> } sort { <sortblock> } ....``` |
| More like this | ```morelike id <id> in <index/type> { fields <fieldsblock> } [optional settings]``` |
| Update | ```update id <id> in <index/type> script <script> [optional settings]``` |
| Register Query| ```<id> into <index> query { <queryblock> }``` |
| Percolate Doc | ```percolate in <index> { fields <fieldsblock> }``` |

#### Create Index

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

#### Indexing

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

#### Searching

#### Percolate

#### Get

#### Deleting

#### Bulk Operations

#### Other

There are other DSLs in play. Validate, update, and explain all have a DSL that is very easy to understand and can be understood from the source.

#### Get

#### Synchronous Operations

All operations are normally async. To switch to a sync client called .sync on the client object. Then all requests will block until the operations has completed. Eg,
```
val resp = client.sync.index { index into "bands" fields ("name"->"coldplay", "debut"->"parachutes") }
```

#### DSL Completeness

As it stands the Scala DSL covers all of the common operations - index, create, delete, delete by query, search, validate, percolate, update, explain, get, and bulk operations. There is good support for the various settings for each of these - more so than the Java client provides in the sense that more settings are provided in a type safe manner. 

However there are settings and operations (mostly admin / cluster related) that the DSL does not yet cover (pull requests welcome!). In these cases it is necessary to drop back to the Java API. This can be done by calling .java on the client object to get the underlying java elastic client, or .admin to get the admin based client, eg, the following request is a Java API request.

```
client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
```

This way you can still access everything the normal Java client covers in the cases where the Scala DSL has no coverage.

## Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```