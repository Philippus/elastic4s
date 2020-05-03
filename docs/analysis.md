# Analyzers

In elasticsearch, [analysis](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis.html) is the process of text into a format that is suitable for search.

It does this by first parsing the text into individual tokens. Often a token is a single word, but it could be a phrase or part of a word.
To do this, Elasticsearch uses a _Tokenizer_.

Next, we have normalization, where tokens that are not normally equal, can be considered equal for search purposes.
For example, `Scala` and `scala` should probably be considered equal in most searches. To do this, Elasticsearch uses
_TokenFilters_ and _CharFilters_.

Elasticsearch provides many built in tokenizer and normalizer combinations, which it calls _analyzers_. For example, the default
analyzer (if none other is specified) is the _Standard Analzyer_. Here is a list of [built in analyzers](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html).

This standard analzyer tokenizes text based on the Unicode Text Segmentation algorithm.
It then normalizes the text into lower case (using a _LowerCaseTokenFilter_).



By default Elasticsearch registers multiple built-in analyzers. These are

* [standard analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-standard-analyzer.html)
* [simple analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-simple-analyzer.html)
* [whitespace analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-whitespace-analyzer.html)
* [stop analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-stop-analyzer.html)
* [keyword analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-keyword-analyzer.html)
* [pattern analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-pattern-analyzer.html)
* [language analyzers](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html)
* [snowball analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-snowball-analyzer.html)

You can use these by simply using the name as defined by elasticsearch. You don't have to do anything to explicity enable them as they are always defined.



Here is an example of using a `WhitespaceAnalyzer` to split up a field on whitespace.

```scala
createIndex("people").mapping(
  properties(
    TextField("name", analyzer = Some("whitespace"))
  )
)
```

This next example uses two analyzers, and one of those is one of the built in language analyzers which are defined in the `LanguageAnalyzers` object.

```scala
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("standard")),
      TextField("bio", analyzer = Some(LanguageAnalyzers.brazilian))
    )
  )
```

Finally, this example uses separate analyzers for indexing and [searching](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-analyzer.html).

```scala
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("standard"), searchAnalyzer = Some("snowball"))
    )
  )
```


### Custom Analzyers

Elasticsearch allows you to create a [custom analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-custom-analyzer.html) which gives you full control over how an analyzer is specified.

Also, there are shortcuts that allow you to use one of the built-in analyzers but with your own options
(for example, if you wanted to use the "stop analzyer" but with your own list of stop words).
Not all built-in analyzers can be modified, eg the WhitespaceAnalyzer has no options.

#### Shortcuts

To use a shortcut, elastic4s makes it very easy.
For example, lets create a mapping for `myindex`, and we want to index each user's name by splitting up on commas.
In this index we will register a pattern analyzer that will split up tokens on commas.

```scala
createIndex("people").mapping(
  properties(
    TextField("name", analyzer = Some("pat1")) // uses the analyzer we define in the next section
  )
).analysis( // analysis block contains all custom analyzers
  Analysis(
    PatternAnalyzer("pat1", regex = ",") // uses the built in pattern analyzer with our own name
  )
)
```

Notice that we gave the analyzer the name `pat1` in the analysis section. This is so we can refer to it in the mapping part.
You can use the same definition in multiple mappings for the same index, and you only need to specify the definition once.
It's also possible to use the same name as one of the built in analyzers. If you do this, then you will override the built-in analyzer.

#### Custom

If the shortcut definitions are still not cutting it, then we can create a fully custom analyzer.

Custom analyzers are created from a single `Tokenizer`, zero or more `TokenFilter`s and zero or more `CharFilter`s
and have a name that is used to refer to them in index mappings.

`CharFilter` are used to preprocess the string of characters before it is passed to the tokenizer.
A character filter may be used to strip out HTML markup, or to convert ```&``` to the token ```and```.

`Tokenizer`s split the incoming string into tokens.
A very simple example (but common use case) is to split a string into tokens on whitespace.
So "Tony Mowbray" would be split into "Tony" and "Mowbray".

`TokenFilter`s apply after the tokenizer, and can modify the incoming tokens.
So if we applied a reverse token filter, then ["Tony", "Mowbray"] would become ["ynoT", "yarbwoM"].


Here is an example of registering a custom analyzer that uses a built in whitespace tokenizer, with lowercase and reverse token filters (just for fun).

```scala
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("custom1"))  // uses the analyzer we define in the next section
    )
  ).analysis(
    Analysis(
      CustomAnalyzer(
        "custom1", // names the analyzer
        tokenizer = "whitespace", // use the built in whitespace tokenizer
        tokenFilters = List("lowercase", "reverse")  // references the token filter names
      )
    )
  )
```

This next example uses a custom tokenizer, which limits all tokens to a maximum of 5 characters.

```scala
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("custom2")) // uses the analyzer we define in the next section
    )
  ).analysis(
    Analysis(
      analyzers = List(
        CustomAnalyzer(
          "custom2", // names the analyzer
          tokenizer = "my_whitespace", // references a custom tokenizer we define in the next section
          tokenFilters = List("lowercase")  // references the token filter names
        )
      ),
      tokenizers = List(
        WhitespaceTokenizer("my_whitespace", maxTokenLength = 5) // registers the custom tokenizer
      )
    )
  )
```

Finally, this example uses the same custom analzyer for multiple fields as well as registering a custom token filter.

```scala
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("super_analyzer")),
      TextField("bio", analyzer = Some("super_analyzer")),
      TextField("address", analyzer = Some("super_analyzer")),
    )
  ).analysis(
    Analysis(
      analyzers = List(
        CustomAnalyzer(
          "super_analyzer", // names the analzyer
          tokenizer = "my_whitespace", // uses the custom tokenizer
          tokenFilters = List("lowercase", "my_edge") // uses a built in token filter and a custom one
        )
      ),
      tokenizers = List(
        WhitespaceTokenizer("my_whitespace", maxTokenLength = 5) // custom tokenizer
      ),
      tokenFilters = List(
        EdgeNGramTokenFilter("my_edge", minGram = 3, maxGram = 4) // custom token filter
      )
    )
  )
```


