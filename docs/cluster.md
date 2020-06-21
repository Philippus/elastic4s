## Cluster

Cluster-level APIs enable us to query the state of the cluster, nodes, shards and so on.

Some cluster-level APIs may operate on a subset of the nodes which can be specified with node filters. For example, the Task Management, Nodes Stats, and Nodes Info APIs can all report results from a filtered set of nodes rather than from all nodes.


### Hot Threads

Queries for hot threads on each node in the cluster.

```scala
client.execute {
  nodeHotThreads()
}
```

Or for a particular node(s)

```scala
client.execute {
  nodeHotThreads("node1")
}
```

For all parameters see [elastic docs](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/cluster-nodes-hot-threads.html#cluster-nodes-hot-threads).


### Nodes Feature Usage

Queries for information on the usage of features.

```scala
client.execute {
  nodeUsage()
}
```

Or for a particular node(s)

```scala
client.execute {
  nodeUsage("node1")
}
```

For all parameters see [elastic docs](https://www.elastic.co/guide/en/elasticsearch/reference/7.8/cluster-nodes-usage.html).

