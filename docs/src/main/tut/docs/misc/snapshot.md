---
layout: docs
title:  "Snapshot"
section: "docs"
---

# Snapshot

Before we can create a snapshot we must register a repository where snapshots will be stored, specifying the type (eg 'fs' for filesystem) and optionally some settings.
We should specify at least the location of the repository. Full settings can be found [here](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/modules-snapshots.html).

This example creates a repostory called "_snapshot" located at "/mount/backup"

```scala
client.execute {
  create repository "_snapshot" `type` "fs" settings Map("location" -> "/mount/backup")
}
```

At some moment in time we can request a snapshot by specifying a name for the snapshot and the repository to save the snapshot to.
In this example waitForCompletion is set which will not complete the future until the the snapshot is completed.
If this is set to false (the default), the future will complete as soon as the request has been received.

```scala
client.execute {
  create snapshot "snap1" in "_snapshot" waitForCompletion true
}
```

Next we can get a specific snapshot's info with:

```scala
client.execute {
  get snapshot "snap1" from "_snapshot"
}
```

Next we can get ALL snapshots information in the repository with:

```scala
client.execute {
  get snapshot Seq() from "_snapshot"
}
```

Later on we can restore the snapshot with:

```scala
client.execute {
  restore snapshot "snap1" from "_snapshot"
}
```

Snapshots can be deleted thus:

```scala
client.execute {
  delete snapshot "snap1" in "_snapshot"
}
```
