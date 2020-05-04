## Validate

The validate request allows a (potentially) expensive query to be validated before it is executed.

```scala
client.execute {
  validateIn("index").query(
    termQuery("name", "sammy")
  )
}
```

Replace the termQuery above with whatever query you wish to validate.

You can also get an explaination of errors by using the `explain` param.

```scala
client.execute {
  validateIn("index").query(
    termQuery("name", "sammy")
  ).explain(true)
}
```

Read the [official docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-validate.html) for the various flags.
