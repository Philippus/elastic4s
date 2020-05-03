## Refresh

When you index a document in Elasticsearch, it is not normally immediately available to be searched, as a _refresh_ has to happen to make it available for the search API.

By default a refresh occurs every second but this can be changed if needed.
Note that this only impacts the visibility of newly indexed documents when using the search API and has nothing
to do with data consistency and durability.


### Using Refresh Policy on indexing

When indexing documents, we can set the `RefreshPolicy` to determine the visibility of the documents.

* RefreshPolicy.IMMEDIATE => Forces a refresh straight after the index operation. The request will not return until elasticsearch has finished the refresh and all documents are available for search.

* RefreshPolicy.WAIT_FOR => The refresh will occur at the regular interval, but the request will not complete until the next refresh has finished and the indexed documents are available for search.

* RefreshPolicy.None => Returns as soon elasticsearch has acknowledged the request. The documents will be available for search only after the next refresh has finished.


Be wary of using IMMEDIATE on heavy loads as you may cause contention with Elasticsearch refreshing too often.


### Change default when creating the index

We can set the default index time when creating the index. For example.

```scala
createIndex(indexname)
  .shards(1)
  .replicas(0)
  .refreshInterval(15.seconds)
```

In this case, 15 seconds is the time between refreshes. Using `RefreshPolicy.IMMEDIATE` would override this, and using `RefreshPolicy.WAIT_FOR` would cause your requests to block for up to 15 seconds.
