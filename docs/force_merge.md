## Force Merge

The force merge API allows to optimize one or more indexes for faster search by merging the number of
Lucene segments (which grow over time). Read more on the
[official documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-forcemerge.html).



There isn't really a whole lot to using this DSL. Simply specify a single index or a sequence of indexes.

```scala
client.execute {
  forceMerge("index1")
}
```

or

```scala
client.execute {
  forceMerge("index1", "index2", ....)
}
```

There are only a few options.

`maxSegments` allows you to choose how many segments to merge to. To merge to a single segment for example, set this value to 1.

```scala
client.execute {
  forceMerge("index1").maxSegments(1)
}
```

If `onlyExpungeDeletes` is set to true, then only expunge segments containing document deletions.

```scala
client.execute {
  forceMerge("index1").onlyExpungeDeletes(true)
}
```
