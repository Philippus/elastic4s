---
layout: docs
title:  "Validate API"
section: "docs"
---

# Validate

The validate API allows us to validate that a query is valid before we execute it. This is an interesting addition
 to elastic4s, because in theory the DSL should not allow you to create invalid queries. 
 However the API is here should you need to use it for whatever reason.
 
An example of validating a simple query
 
```scala
client.execute {
  validate in "index" query {
      termQuery("name", "sammy")
  }
}
```

Simply replace termQuery with whatever query you wish to validate.

You can also get an explaination of errors by using the explain param

```scala
client.execute {
  validate in "index" query {
      termQuery("name", "sammy")
  } explain true
}
```
