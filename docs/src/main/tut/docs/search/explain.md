---
layout: docs
title:  "Explain API"
section: "docs"
---

# Explain

The explain request allows us to see why a document would or would not be included in a query.
This is useful to see why queries are not performing as expected.

The format is simple. Specify the index, type and id of the document, and attach a query from the queryDSL.

```scala
val resp = client.execute {
  explain id 8 in "beer/lager" query termQuery("name","budweiser")
}
```

Which would return something like

```json
{
  "ok" : true,
  "matches" : true,
  "explanation" : {
    "value" : 0.15342641,
    "description" : "fieldWeight(name:budweiser in 0), product of:",
    "details" : [ {
      "value" : 1.0,
      "description" : "tf(termFreq(name:budweiser)=1)"
    }, {
      "value" : 0.30685282,
      "description" : "idf(docFreq=1, maxDocs=1)"
    }, {
      "value" : 0.5,
      "description" : "fieldNorm(field=name, doc=0)"
    } ]
  }
}
```
