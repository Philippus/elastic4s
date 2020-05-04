---
layout: docs
title:  "Suggestions API"
section: "docs"
---

# Suggestions

The suggestions feature returns similar looking terms (suggestions) for some given text using a suggestor. Suggestions
can be specified as part of a search query.

We can ask elasticsearch to include suggestions by adding a `suggestions` block to the search query. Inside the suggestions block 
we add suggestion entries, of type `term`, `fuzzyCompletion`, `completion`, or `phrase`.
 
You can have multiple suggestions (for different fields say, or different types) in a search query, so each suggestion  
 needs a name, which is used to refer to it later. Elastic4s also allows you to look up the suggestion response from the original 
 suggestion class, which is the preferred way as it also avoids some casting you would otherwise need to do.
 
## Term Suggestion

The term suggestor looks for terms in the index that are closest to the input.
Let's start by creating an index with some data.

```scala
client.execute(
  bulk(
    index into indexType source Song("style", "taylor swift"),
    index into indexType source Song("shake it off", "Taylor Swift"),
    index into indexType source Song("a new england", "kirsty maccoll"),
    index into indexType source Song("blank page", "taylor swift"),
    index into indexType source Song("I want it all", "Queen"),
    index into indexType source Song("I to break free", "Queen"),
    index into indexType source Song("radio gaga", "Queen"),
    index into indexType source Song("we are the champions", "Quoon"),
    index into indexType source Song("Down with the trumpets", "Rizzle Kicks"),
    index into indexType source Song("Down with the trombones", "Razzle Kacks"),
    index into indexType source Song("lover of the light", "Mumford and sons"),
    index into indexType source Song("Monster", "Mumford and sons")
  )
).await // as always the await is just used to block in demo code
```

Now we can add a suggestion block that will work with the data above.

```scala
val mysugg = termSuggestion("mysugg").field("artist").text("taylor swaft")

val resp = client.execute {
  search in indexType suggestions {
    mysugg
  }
}.await

// use the suggestion def created earlier to retrieve the suggestion response
resp.suggestion(mysugg).entry("taylor").options // is empty
resp.suggestion(mysugg).entry("swaft").options // contains ["swift"]
```

In the suggestion response are entries, where each entry refers to a term in the text supplied. So in that example, 
the text was "taylor swaft" which will become two terms, "taylor" and "swaft" so the suggestion response also has
two entries. The options array is the actual suggested terms for the entry.

You will notice that the first time, "taylor", has no options. This is because it actually had a match, so by default
elasticsearch skips it. You can force suggestions even when the term matches by changing the mode:

```scala
val mysugg = termSuggestion("mysugg").field("artist").text("taylor swaft").mode(SuggestMode.Always)
```

`SuggestMode.Always` means always include suggestions, and SuggestMode.Popular means include suggestions if there were
more popular results than the ones that matched.
