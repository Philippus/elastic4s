---
layout: docs
title:  "Document traits"
section: "docs"
---

# Document Traits

Sometimes it is useful to seperate the knowledge of the type from the indexing logic. For this we can use the
`DocumentSource` or `DocumentMap` abstraction to encapsulate the conversion logic.

These are simply traits that you can mix into your classes which provide a method that elastic4s
can use to populate an index request.

For example. using `DocumentMap` is as simple as defining a class like this.

```scala
case class Band(name:String, albums:Seq[String], label:String) extends DocumentMap {
  def map = Map("name" -> name, "albums"->albums.mkString(" "), "label" -> label)
}
```

And then using that class with `doc` in an index request.

```scala
case class Band(name: String, albums: Seq[String], label: String)
val band = Band("coldplay", Seq("X&Y", "Parachutes"), "Parlophone")

client.execute {
  // the band object will be implicitly converted into a DocumentSource
  index into "music/bands" doc band
}
```

`DocumentSource` is similar except you return a JSON string rather than a Scala Map. Use whatever you prefer.

If you want to index directly from a Jackson JSON object then you can use the built in JacksonSource wrapper.

```scala
val myJsonDoc = ... // some jackson object
client.execute { index into "electronics/phones" doc JacksonSource(myJsonDoc) }
```

Or you can even index plain objects and elastic4s will use Jackson to marshall into JSON.
This uses the Scala extension in Jackson and so supports scala collections, options, etc.

```scala
val anyOldObject = ... // anything that extends from AnyRef
client.execute { index into "electronics/phones" doc anyOldObject }
```
