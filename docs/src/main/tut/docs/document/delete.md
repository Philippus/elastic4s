---
layout: docs
title:  "Delete API"
section: "docs"
---

# Deleting

A delete request allows us to delete a document from an index based on an id. We need to know the type and the index. Then we can issue a query such as

```scala
  client.execute {
    delete(id = 3) from "places" / "cities"
  }
```

Delete is bulk compatible so we can issue multiple requests at once:

```scala
  client.bulk {
    delete(id = 3) from "places" / "cities",
    delete(id = 8) from "places" / "cities",
    delete(id = 3) from "music" / "bands"
  }
```


## Index

To delete an entire index you can use `deleteIndex`:

```scala
  client.execute { deleteIndex("places") }
  client.execute { deleteIndex("_all") } // Deletes ALL indices!
  // Or alternatively:
  client.execute { delete index "places" }
  client.execute { delete index ("places", "countries") } // Deletes two indices
```
