## Get

The get request allows us to retrieve a document from an index by id.

In this example we are retrieving the document with id 'coldplay' from the `bands` index.

```scala
client.execute {
  get("bands", "coldplay")
}
```
You can specify a version, which means the GET will only succeed if the version matches.

```scala
client.execute {
  get("bands", "coldplay").version(12)
}
```

If the document exists with version 12 then this will return a result, otherwise it will return no results.

Other options are realtime, routing, preference, versionType, fetchSourceContext. For more details on what these do, consult the official elasticsearch documents [here](http://www.elasticsearch.org/guide/en/elasticsearch/reference/master/docs-get.html).

### Multiget

Get requests can be wrapped in a multiget query. Read more [here](multiget.md).
