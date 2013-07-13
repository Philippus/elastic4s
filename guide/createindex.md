### Creating Indexes

Elastic4s does not require us to define indexes or their fields in advance. Indexes are created when first needed and fields are added when documents are indexed.
Sometimes though we need to specify fields if we want non-default behaviour, such as changing the way a field is analyzed or a default null option.
This is done through what elastic calls mappings.

Note: if we specify a mapping for an index, the index still remains dynamic for other fields. Think of the mapping as an override, anything not specified operates as normal.

One of most common things we want to define is the field type, this is useful for sorting for example.
Let's define an index with a single field, of type Integer.

```scala
  client.execute {
    create index "places" mappings (
      "cities" as (
        "year_founded" typed IntegerType
      )
    )
  }
```

We created the index, and a mapping for a type called "cities", and we defined one field, "year_founded" as an Integer field.

Lets enhance this by adding another field called location which is a GeoPointType. This is a very useful field type for doing distance type querying (find places within 'x miles' for example).

```scala
  client.execute {
    create index "places" mappings (
      "cities" as (
        "year_founded" typed IntegerType,
        "location" typed GeoPointType
      )
    )
  }
```

So now we have two defined fields (remember, other fields will be added dynamically if required, with default options).

Sidepoint: You might wonder why the DSL keyword is called typed. This is because type is a keyword in scala and typed looks nicer than ```"location" `type` GeoPointType.```

Now some places have names that include common stop words. For example "The Congo" or "Bay of Biscay".
We might to search on these so we want to change the analyzer used to include all words. Therefore we'll use a SimpleAnalyzer which simply lowercases the text and splits on non letters.
So in this next example we'll add a new type, called countries, and we'll change the analyzer as mentioned.

```scala
  client.execute {
    create index "places" mappings (
      "cities" as (
        "year_founded" typed IntegerType,
        "location" typed GeoPointType
      ), // note trailing comma as this is a var args invocation
      "countries" as (
        "name" analyzer SimpleAnalyzer
      )
    )
  }
```

The available analzyers are WhitespaceAnalyzer, StandardAnalyzer, SimpleAnalyzer, StopAnalyzer, KeywordAnalyzer, PatternAnalyzer, SnowballAnalyzer.
You can actually define your own analyzers (they are really just combinations of filters and tokenizers).

To set the number of shards and or replicas, we can do this at the index level.

```scala
client.execute { create index "places" shards 3 replicas 2 ... // mappings ... }
```

ElasticSearch is a clever beast. One small example is that it can detect dates automatically in fields instead of us
setting the DateType in mappings. This is useful when you don't know all the fields in advance.
To set the formats that elastic will recognize, we can use ```date_detection``` at the mapping level.
Going back to our places example we'll let elastic detect our dates from two different formats (you can set as many as you need).

```scala
  client.execute {
    create index "places" mappings (
      "cities" as (
        "year_founded" typed IntegerType,
        "location" typed GeoPointType
      ),
      "countries" as (
        "name" analyzer SimpleAnalyzer
      ) dateDetection true dynamicDateFormats("dd/MM/yyyy", "dd-MM-yyyy")
    )
  }
```

Wrapping up our introduction, we finally want to add a field called [demonym](http://en.wikipedia.org/wiki/Demonym) to the cities.
If there is no demonym (ie, the field is empty / null when indexing) we'll tell elastic to use a default of "citizen".
We definitely don't want any stemming on these words. If you're from Brussels, you are a Bruxellois. We don't want the s to be removed.
Therefore we'll also tell elastic to index the whole word as a single token.

```scala
  client.execute {
    create index "places" mappings (
      "cities" as (
        "year_founded" typed IntegerType,
        "location" typed GeoPointType,
        "demonym" nullValue "citizen" analyzer KeywordAnalyzer
      ),
      "countries" as (
        "name" analyzer SimpleAnalyzer
      ) dateDetection true dynamicDateFormats("dd/MM/yyyy", "dd-MM-yyyy")
    )
  }
```

There are many options one can set on a field. For the full list, see the methods in the DSL or see the official documentation on [mappings](http://www.elasticsearch.org/guide/reference/mapping) (the DSL keywords will be the same or very close to the name of the optional in the REST API).