### Deleting

A delete request allows us to delete a document from an index based on either an id or a query.

To delete a document by id, we need to know the type and the index. Then we can issue a query such as

```scala
  client.execute {
    delete id 3 from "places/cities"
  }
```

or

```scala
  client.execute {
    delete id 3 from "places"-> "cities"
  }
```

The syntax is quite SQL like. Of course we don't always want to delete by id, we might want to do a delete based on
some criteria. Enter the delete by query request.

Let's delete all London's from the cities index.

```scala
  client.execute {
    delete from "places" -> "cities" where "name:london"
  }
```

The query (the where clause) can be any type of query definition. Lets do something similar with a regex query.

```scala
  client.execute {
    delete from "places" -> "cities" where regexQuery("name", "Lond*")
  }
```

Delete is bulk compatible so we can issue multiple requests at once

```scala
  client.bulk {
    delete id 3 from "places/cities",
    delete id 8 from "places/cities",
    delete id 3 from "music/bands"
  }
```

You'll notice that the format for the query construct is exactly the same as for the search operation.
That is the "where" keyword accepts any query definition, so that's any of the queries you can use wih the search DSL.
In fact the same constructs can be used by any operation that requires a query - search, delete, percolate, explain, filters, etc. The same Query DSL is used throughout.

Finally, if we want our query to execute across multiple indexes and types we can use this format

```scala
  client.execute {
    delete from "places" types Seq("cities", "countries") where "continent:Europe"
  }
```
  or

```scala
  client.execute {
    delete from "places".types("cities", "countries").where("continent:Europe")
  }
```