### Deleting

A delete request allows us to delete a document from an index based on either an id or a query.

To delete a document by id, we need to know the type and the index. Then we can issue a query such as

```scala
  client.delete {
    "places/cities" -> 3
  }
```

Delete is bulk compatible so we can issue multiple requests at once

```scala
  client.delete {
    "places/cities" -> 4,
    "places/cities" -> 1,
    "music/bands" -> 19
  }
```

Of course we don't always want to delete by id, we might want to do a delete based on some criteria. Enter the
delete by query request.

Let's delete all London's from the cities index.

```scala
  client.delete {
    "places" types "cities" where "name:london"
  }
```

The query (the where clause) can be any type of query definition. Lets do something similar with a regex query (slow!)

```scala
  client.delete {
    "places" types "cities" where regexQuery("name", "Lond*")
  }
```

You'll notice that the format for the query construct is exactly the same as for the search operation.
That is the "where" keyword accepts any query definition, so that's any of the queries you can use wih the search DSL.
In fact the same constructs can be used by any operation that requires a query - search, delete, percolate, explain, filters, etc. The same Query DSL is used throughout.