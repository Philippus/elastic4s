# elastic4s - Elasticsearch Scala Client

[![build](https://github.com/Philippus/elastic4s/workflows/master/badge.svg)](https://github.com/Philippus/elastic4s/actions/workflows/master.yml?query=workflow%3Amaster+branch%3Amaster)
![Current Version](https://img.shields.io/badge/version-8.13.0-brightgreen.svg?style=flat "8.13.0")
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat "Apache 2.0")](LICENSE)

**This is a community project - PRs will be accepted and releases published by the maintainer**

Elastic4s is a concise, idiomatic, reactive, type safe Scala client for Elasticsearch. The official Elasticsearch Java client can of course be used in Scala, but due to Java's syntax it is more verbose and it naturally doesn't support classes in the core Scala core library nor Scala idioms such as typeclass support.

Elastic4s's DSL allows you to construct your requests programatically, with syntactic and semantic errors manifested at compile time, and uses standard Scala futures to enable you to easily integrate into an asynchronous workflow. The aim of the DSL is that requests are written in a builder-like way, while staying broadly similar to the Java API or Rest API. Each request is an immutable object, so you can create requests and safely reuse them, or further copy them for derived requests. Because each request is strongly typed your IDE or editor can use the type information to show you what operations are available for any request type.

Elastic4s supports Scala collections so you don't have to do tedious conversions from your Scala domain classes into Java collections. It also allows you to index and read classes directly using typeclasses so you don't have to set fields or json documents manually. These typeclasses are generated using your favourite json library - modules exist for Jackson, Circe, Json4s, PlayJson and Spray Json. The client also uses standard Scala durations to avoid the use of strings or primitives for duration lengths.

#### Key points

* Type safe concise DSL
* Integrates with standard Scala futures or other effects libraries
* Uses Scala collections library over Java collections
* Returns `Option` where the java methods would return null
* Uses Scala `Duration`s instead of strings/longs for time values
* Supports typeclasses for indexing, updating, and search backed by Jackson, Circe, Json4s, PlayJson and Spray Json implementations
* Supports Java and Scala HTTP clients such as Akka-Http
* Provides [reactive-streams](#elastic-reactive-streams) implementation
* Provides a testkit subproject ideal for your tests

### Release

Current Elastic4s versions support Scala 2.12 and 2.13. Scala 2.10 support has been dropped starting with 5.0.x and Scala 2.11 has been dropped starting with 7.2.0. For releases that are compatible with earlier versions of Elasticsearch,
[search maven central](http://search.maven.org/#search|ga|1|g%3A%22com.sksamuel.elastic4s%22).

Note that starting from version 8.12.0 the group id has changed from com.sksamuel.elastic4s to nl.gn0s1s.


| Elastic Version | Scala 2.12                                                                                                                                                                                                                                                                                                                   | Scala 2.13                                                                                                                                                                                                                                                                                                                   | Scala 3                                                                                                                                                                                                            |
|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 8.13.x          | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_2.12/8.13.svg?label=latest%208.13%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                            | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_2.13/8.13.svg?label=latest%208.13%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                            | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_3/8.13.svg?label=latest%208.13%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)              |
| 8.12.x          | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_2.12/8.12.svg?label=latest%208.12%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                            | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_2.13/8.12.svg?label=latest%208.12%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                            | [<img src="https://img.shields.io/maven-central/v/nl.gn0s1s/elastic4s-core_3/8.12.svg?label=latest%208.12%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)              |
| 8.11.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.11.svg?label=latest%208.11%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.11.svg?label=latest%208.11%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.11.svg?label=latest%208.11%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3) |
| 8.10.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.10.svg?label=latest%208.10%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.10.svg?label=latest%208.10%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.10.svg?label=latest%208.10%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3) |
| 8.9.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.9.svg?label=latest%208.9%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.9.svg?label=latest%208.9%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.9.svg?label=latest%208.9%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.8.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.8.svg?label=latest%208.8%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.8.svg?label=latest%208.8%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.8.svg?label=latest%208.8%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.7.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.7.svg?label=latest%208.7%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.7.svg?label=latest%208.7%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.7.svg?label=latest%208.7%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.6.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.6.svg?label=latest%208.6%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.6.svg?label=latest%208.6%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.6.svg?label=latest%208.6%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.5.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.5.svg?label=latest%208.5%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.5.svg?label=latest%208.5%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.5.svg?label=latest%208.5%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.4.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.4.svg?label=latest%208.4%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.4.svg?label=latest%208.4%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_3/8.4.svg?label=latest%208.4%20release%20for%203"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_3)   |
| 8.3.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.3.svg?label=latest%208.3%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.3.svg?label=latest%208.3%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 8.2.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.2.svg?label=latest%208.2%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.2.svg?label=latest%208.2%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 8.1.x           | [<img src="https://img.shields.io/maven-metadata/v.svg?label=latest%208.1%20release%20for%202.12&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fsksamuel%2Felastic4s%2Felastic4s-core_2.12%2Fmaven-metadata.xml&versionPrefix=8.1."/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22) | [<img src="https://img.shields.io/maven-metadata/v.svg?label=latest%208.1%20release%20for%202.13&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fsksamuel%2Felastic4s%2Felastic4s-core_2.13%2Fmaven-metadata.xml&versionPrefix=8.1."/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22) |
| 8.0.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/8.0.svg?label=latest%208.0%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/8.0.svg?label=latest%208.0%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.17.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.17.svg?label=latest%207.17%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.17.svg?label=latest%207.17%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.16.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.16.svg?label=latest%207.16%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.16.svg?label=latest%207.16%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.15.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.15.svg?label=latest%207.15%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.15.svg?label=latest%207.15%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.14.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.14.svg?label=latest%207.14%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.14.svg?label=latest%207.14%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.13.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.13.svg?label=latest%207.13%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.13.svg?label=latest%207.13%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.12.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.12.svg?label=latest%207.12%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.12.svg?label=latest%207.12%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.11.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.11.svg?label=latest%207.11%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.11.svg?label=latest%207.11%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.10.x          | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.10.svg?label=latest%207.10%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                               | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.10.svg?label=latest%207.10%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                               |
| 7.9.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.9.svg?label=latest%207.9%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.9.svg?label=latest%207.9%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.8.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.8.svg?label=latest%207.8%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.8.svg?label=latest%207.8%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.7.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.7.svg?label=latest%207.7%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.7.svg?label=latest%207.7%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.6.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.6.svg?label=latest%207.6%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.6.svg?label=latest%207.6%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.5.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.5.svg?label=latest%207.5%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.5.svg?label=latest%207.5%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.4.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.4.svg?label=latest%207.4%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.4.svg?label=latest%207.4%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.3.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.3.svg?label=latest%207.3%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.3.svg?label=latest%207.3%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.2.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.2.svg?label=latest%207.2%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.2.svg?label=latest%207.2%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |
| 7.1.x           | [<img src="https://img.shields.io/maven-metadata/v.svg?label=latest%207.1%20release%20for%202.12&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fsksamuel%2Felastic4s%2Felastic4s-core_2.12%2Fmaven-metadata.xml&versionPrefix=7.1."/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22) | [<img src="https://img.shields.io/maven-metadata/v.svg?label=latest%207.1%20release%20for%202.13&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Fsksamuel%2Felastic4s%2Felastic4s-core_2.13%2Fmaven-metadata.xml&versionPrefix=7.1."/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22) |
| 7.0.x           | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.12/7.0.svg?label=latest%207.0%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.12%22)                                                                                                 | [<img src="https://img.shields.io/maven-central/v/com.sksamuel.elastic4s/elastic4s-core_2.13/7.0.svg?label=latest%207.0%20release%20for%202.13"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22elastic4s-core_2.13%22)                                                                                                 |

For releases prior to 7.0 [search maven central](https://search.maven.org/search?q=elastic4s).

## Quick Start

We have created sample projects in both sbt, maven and gradle. Check them out here:
https://github.com/philippus/elastic4s/tree/master/samples

To get started you will need to add a dependency:

* [elastic4s-client-esjava](https://mvnrepository.com/artifact/com.sksamuel.elastic4s/a:elastic4s-client-esjava)

```scala
// major.minor are in sync with the elasticsearch releases
val elastic4sVersion = "x.x.x"
libraryDependencies ++= Seq(
  // recommended client for beginners
  "nl.gn0s1s" %% "elastic4s-client-esjava" % elastic4sVersion,
  // test kit
  "nl.gn0s1s" %% "elastic4s-testkit" % elastic4sVersion % "test"
)
```

The basic usage is that you create an instance of a client and then invoke the `execute` method with the requests you
want to perform. The execute method is asynchronous and will return a standard Scala `Future[T]`
(or use one of the [Alternative executors](#alternative-executors)) where T is the response
type appropriate for your request type. For example a _search_ request will return a response of type `SearchResponse`
which contains the results of the search.

To create an instance of the HTTP client, use the `ElasticClient` companion object methods.
Requests are created using the elastic4s DSL. For example to create a search request, you would do:

```scala
search("index").query("findthistext")
```

The DSL methods are located in the `ElasticDsl` trait which needs to be imported or extended.

```scala
import com.sksamuel.elastic4s.ElasticDsl._
```


## Creating a Client

The entry point in elastic4s is an instance of `ElasticClient`.
This class is used to execute requests, such as `SearchRequest`, against an Elasticsearch cluster and returns a response type such as `SearchResponse`.

`ElasticClient` takes care of transforming the requests and responses, and handling success and failure, but the actual HTTP functions are delegated to a HTTP library.
One such library is `JavaClient` which uses the http client provided by the offical Java elasticsearch library.

So, to connect to an ElasticSearch cluster, pass an instance of `JavaClient` to an `ElasticClient`.
`JavaClient` is configured using `ElasticProperties` in which you can specify protocol, host, and port in a single string.

```scala
val props = ElasticProperties("http://host1:9200")
val client = ElasticClient(JavaClient(props))
```

For multiple nodes you can pass a comma-separated list of endpoints in a single string:

```scala
val nodes = ElasticProperties("http://host1:9200,host2:9200,host3:9200")
val client = ElasticClient(JavaClient(nodes))
```

There are several http libraries to choose from, or you can wrap any HTTP library you wish. For further details, and information
on how to specify credentials and other options, see [the full client documentation](docs/clients.md)


## Example Application

An example is worth 1000 characters so here is a quick example of how to connect to a node with a client, create an
index and index a one field document. Then we will search for that document using a simple text query.

**Note:** As of version `0.7.x` the `LocalNode` functionality has been removed. It is recommended that you stand up
a local ElasticSearch Docker container for development. This is the same strategy used in the [tests](https://github.com/philippus/elastic4s/blob/master/elastic4s-testkit/src/main/scala/com/sksamuel/elastic4s/testkit/DockerTests.scala).

```scala
import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.SearchResponse

object ArtistIndex extends App {

  // in this example we create a client to a local Docker container at localhost:9200
  val client = ElasticClient(JavaClient(ElasticProperties(s"http://${sys.env.getOrElse("ES_HOST", "127.0.0.1")}:${sys.env.getOrElse("ES_PORT", "9200")}")))

  // we must import the dsl
  import com.sksamuel.elastic4s.ElasticDsl._

  // Next we create an index in advance ready to receive documents.
  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block
  // the calling thread but is useful when testing
  client.execute {
    createIndex("artists").mapping(
      properties(
        TextField("name")
      )
    )
  }.await

  // Next we index a single document which is just the name of an Artist.
  // The RefreshPolicy.Immediate means that we want this document to flush to the disk immediately.
  // see the section on Eventual Consistency.
  client.execute {
    indexInto("artists").fields("name" -> "L.S. Lowry").refresh(RefreshPolicy.Immediate)
  }.await

  // now we can search for the document we just indexed
  val resp = client.execute {
    search("artists").query("lowry")
  }.await

  // resp is a Response[+U] ADT consisting of either a RequestFailure containing the
  // Elasticsearch error details, or a RequestSuccess[U] that depends on the type of request.
  // In this case it is a RequestSuccess[SearchResponse]

  println("---- Search Results ----")
  resp match {
    case failure: RequestFailure => println("We failed " + failure.error)
    case results: RequestSuccess[SearchResponse] => println(results.result.hits.hits.toList)
    case results: RequestSuccess[_] => println(results.result)
  }

  // Response also supports familiar combinators like map / flatMap / foreach:
  resp foreach (search => println(s"There were ${search.totalHits} total hits"))

  client.close()
}

```


## Alternative Executors

By default, elastic4s uses scala `Future`s when returning responses, but any effect type can be supported.

If you wish to use ZIO, Cats-Effect, Monix or Scalaz, then read this page on [alternative effects](docs/effects.md).





## Index Refreshing

When you index a document in Elasticsearch, usually it is not immediately available to be searched, as a _refresh_ has to happen to make it visible to the search API.

By default a refresh occurs every second but this can be changed if needed.
Note that this only impacts the visibility of newly indexed documents and has nothing
to do with data consistency and durability.

This setting can be [controlled](docs/refresh.md) when creating an index or when indexed documents.





## Create Index

All documents in Elasticsearch are stored in an index. We do not need to tell Elasticsearch in advance what an index
will look like (eg what fields it will contain) as Elasticsearch will adapt the index dynamically as more documents are added, but we must at least create the index first.

To create an index called "places" that is fully dynamic we can simply use:

```scala
client.execute {
  createIndex("places")
}
```

We can optionally set the number of shards and/or replicas

```scala
client.execute {
  createIndex("places").shards(3).replicas(2)
}
```

Sometimes we want to specify the properties of the fields in the index in advance.
This allows us to manually set the type of the field (where Elasticsearch might infer something else) or set the analyzer used,
or multiple other options

To do this we add mappings:

```scala
client.execute {
    createIndex("cities").mapping(
        properties(
            keywordField("id"),
            textField("name").boost(4),
            textField("content"),
            keywordField("country"),
            keywordField("continent")
        )
    )
}
```

Then Elasticsearch is preconfigured with those mappings for those fields.
It is still fully dynamic and other fields will be created as needed with default options. Only the fields specified will have their type preset.







## Analyzers

Analyzers control how Elasticsearch parses the fields for indexing. For example, you might decide that you want
whitespace to be important, so that "band of brothers" is indexed as a single "word" rather than the default which is
to split on whitespace. There are many advanced options available in analayzers. Elasticsearch also allows us to create
custom analyzers. For more details [see the documentation on analyzers](docs/analysis.md).





## Indexing

To index a document we need to specify the index and type and optionally we can set an id.
If we don't include an id then elasticsearch will generate one for us.
We must also include at least one field. Fields are specified as standard tuples.

```scala
client.execute {
  indexInto("cities").id("123").fields(
    "name" -> "London",
    "country" -> "United Kingdom",
    "continent" -> "Europe",
    "status" -> "Awesome"
  )
}
```

There are many additional options we can set such as routing, version, parent, timestamp and op type.
See [official documentation](http://www.elasticsearch.org/guide/reference/api/index_/) for additional options, all of
which exist in the DSL as keywords that reflect their name in the official API.





## Indexable Typeclass

Sometimes it is useful to create documents directly from your domain model instead of manually creating maps of fields.
To achieve this, elastic4s provides the `Indexable` typeclass.

If you provide an implicit instance of `Indexable[T]` in scope for any
class T that you wish to index, and then you can invoke `doc(t)` on the `IndexRequest`.

For example:

```scala
// a simple example of a domain model
case class Character(name: String, location: String)

// turn instances of characters into json
implicit object CharacterIndexable extends Indexable[Character] {
  override def json(t: Character): String = s""" { "name" : "${t.name}", "location" : "${t.location}" } """
}

// now index requests can directly use characters as docs
val jonsnow = Character("jon snow", "the wall")
client.execute {
  indexInto("gameofthrones").doc(jonsnow)
}
```

Some people prefer to write typeclasses manually for the types they need to support. Other people like to just have
it done automagically. For the latter, elastic4s provides extensions for the well known Scala Json libraries that
can be used to generate Json generically.

To use this, add the import for your chosen library below and bring the implicits into scope. Then you can pass any case class
instance to `doc` and an `Indexable` will be derived automatically.

| Library | Elastic4s Module | Import |
|---------|------------------|--------|
|[Jackson](https://github.com/FasterXML/jackson-module-scala)|[elastic4s-json-jackson](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-jackson)|import ElasticJackson.Implicits._|
|[Json4s](http://json4s.org/)|[elastic4s-json-json4s](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-json4s)|import ElasticJson4s.Implicits._|
|[Circe](https://github.com/travisbrown/circe)|[elastic4s-json-circe](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-circe)|import io.circe.generic.auto._ <br/>import com.sksamuel.elastic4s.circe._|
|[PlayJson](https://github.com/playframework/play-json)|[elastic4s-json-play](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-play)|import com.sksamuel.elastic4s.playjson._|
|[Spray Json](https://github.com/spray/spray-json)|[elastic4s-json-spray](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-spray)|import com.sksamuel.elastic4s.sprayjson._|
|[ZIO 1.0 Json](https://github.com/zio/zio-json)|[elastic4s-json-zio-1](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-zio-1)|import com.sksamuel.elastic4s.ziojson._|
|[ZIO 2.0 Json](https://github.com/zio/zio-json)|[elastic4s-json-zio](http://search.maven.org/#search%7Cga%7C1%7Celastic4s-json-zio)|import com.sksamuel.elastic4s.ziojson._|





## Searching

To execute a [search](http://www.elasticsearch.org/guide/reference/api/search/) in elastic4s, we need to pass an instance of `SearchRequest` to our client.

One way to do this is to invoke `search` and pass in the index name. From there, you can call
`query` and pass in the type of query you want to perform.

For example, to perform a simple text search, where the query is parsed from a single string we can do:

```scala
client.execute {
  search("cities").query("London")
}
```

For full details on creating queries and other search capabilities such source filtering and aggregations, please read [this](docs/search.md).



## Multisearch

Multiple search requests can be executed in a single call using the [multisearch](docs/multisearch.md) request type. This is the search equivilent of the bulk request.



## HitReader Typeclass

By default Elasticsearch search responses contain an array of `SearchHit` instances which contain things like the id,
index, type, version, etc as well as the document source as a string or map. Elastic4s provides a means to convert these
back to meaningful domain types quite easily using the `HitReader[T]` typeclass.

Provide an implementation of this typeclass, as an in scope implicit, for whatever type you wish to marshall search responses into, and then you can call `to[T]` or `safeTo[T]` on the response.
The difference between `to` and `safeTo` is that `to` will drop any errors and just return successful conversions, whereas safeTo returns
a sequence of `Either[Throwable, T]`.

A full example:

```scala
case class Character(name: String, location: String)

implicit object CharacterHitReader extends HitReader[Character] {
  override def read(hit: Hit): Either[Throwable, Character] = {
    val source = hit.sourceAsMap
    Right(Character(source("name").toString, source("location").toString))
  }
}

val resp = client.execute {
  search("gameofthrones").query("kings landing")
}.await // don't block in real code

// .to[Character] will look for an implicit HitReader[Character] in scope
// and then convert all the hits into Characters for us.
val characters: Seq[Character] = resp.result.to[Character]
```

This is basically the inverse of the `Indexable` typeclass. And just like Indexable, the json modules provide implementations
out of the box for any types. The imports are the same as for the Indexable typeclasses.

As a bonus feature of the Jackson implementation, if your domain object has fields called `_timestamp`, `_id`, `_type`, `_index`, or
`_version` then those special fields will be automatically populated as well.




## Highlighting

Elasticsearch can annotate results to show which part of the results matched the queries by using highlighting.
Just think when you're in google and you see the snippets underneath your results - that's what highlighting does.

We can use this very easily, just add a highlighting definition to your search request, where you set the field or fields to be highlighted. Viz:

```scala
search("music").query("kate bush").highlighting (
  highlight("body").fragmentSize(20)
)
```

All very straightforward. There are many options you can use to tweak the results. In the example above I have
simply set the snippets to be taken from the field called "body" and to have max length 20. You can set the number of fragments to return, seperate queries to generate them and other things. See the elasticsearch page on [highlighting](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-highlighting.html) for more info.





## Get / Multiget

A [get](docs/get.md) request allows us to retrieve a document directly by id.

```scala
client.execute {
  get("bands", "coldplay")
}
```

We can fetch multiple documents at once using the [multiget](docs/multiget.md) request.




## Deleting

In elasticsearch we can delete based on an id, or based on a query (which can match multiple documents).

See more about [delete](docs/delete.md).



## Updates

We can update existing documents without having to do a full index, by updating a partial set of fields.
We can _update-by-id_ or _update-by-query_.

For more details see the [update](docs/update.md) page.

## More like this

If you want to return documents that are "similar" to  a current document we can do that very easily with the more like this query.

```scala
client.execute {
  search("drinks").query {
    moreLikeThisQuery("name").likeTexts("coors", "beer", "molson").minTermFreq(1).minDocFreq(1)
  }
}
```

For all the options see [here](http://www.elasticsearch.org/guide/reference/query-dsl/mlt-query/).



## Count

A [count request](docs/count.md) executes a query and returns a count of the number of matching documents for that query.




## Bulk Operations

Elasticsearch is fast. Roundtrips are not. Sometimes we want to wrestle every last inch of performance and a useful way
to do this is to batch up requests. We can do this in elasticsearch via the bulk API. A bulk request wraps index,
delete and update requests in a single request.

```scala
client.execute {
  bulk(
    indexInto("bands").fields("name" -> "coldplay"), // one index request
    deleteById("bands", "123"), // a delete by id request
    indexInto("bands").fields( // second index request
      "name" -> "elton john",
      "best_album" -> "tumbleweed connection"
    )
  )
}
```
A single HTTP request is now needed for 3 operations. In addition Elasticsearch can now optimize the requests,
by combining inserts or using aggressive caching.

For full details see the [docs on bulk operations](docs/bulk.md).






## Show Query JSON

It can be useful to see the json output of requests in case you wish to tinker with the request in a REST client or your browser. It can be much easier to tweak a complicated query when you have the instant feedback of the HTTP interface.

Elastic4s makes it easy to get this json where possible. Simply invoke the `show` method on the client with a request to get back a json string. Eg:

```scala
val json = client.show {
  search("music").query("coldplay")
}
println(json)
```

Not all requests have a json body. For example _get-by-id_ is modelled purely by http query parameters, there is no json body to output. And some requests aren't supported by the show method - you will get an implicit not found error during compliation if that is the case




## Aliases

An [index alias](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html) is a logical name used to reference one or more indices. Most Elasticsearch APIs accept an index alias in place of an index name.

For elastic4s syntax for aliases [click here](docs/alias.md).




## Explain

An [explain request](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-explain.html) computes a score explanation for a query and a specific document. This can give useful feedback whether a document matches or didn’t match a specific query.

For elastic4s syntax for explain [click here](docs/explain.md).




## Validate Query

The validate query request type allows you to check a query is valid before executing it.

See the syntax [here](docs/validate.md).







## Force Merge

Merging reduces the number of segments in each shard by merging some of them together, and also frees up the space used by deleted documents. Merging normally happens automatically, but sometimes it is useful to trigger a merge manually.

See the syntax [here](docs/force_merge.md).





## Cluster APIs

Elasticsearch supports querying the state of the cluster itself, to find out information on nodes, shards, indices, tasks and so on. See the range of cluster APIs [here](docs/cluster.md).





## Search Iterator

Sometimes you may wish to iterate over all the results in a search, without worrying too much about handling futures, and re-requesting
via a scroll. The `SearchIterator` will do this for you, although it will block between requests. A search iterator is just an implementation
of `scala.collection.Iterator` backed by elasticsearch queries.

To create one, use the iterate method on the companion object, passing in the http client, and a search request to execute. The
search request must specify a keep alive value (which is used by elasticsearch for scrolling).

```scala
implicit val reader : HitReader[MyType] =  ...
val iterator = SearchIterator.iterate[MyType](client, search(index).matchAllQuery.keepAlive("1m").size(50))
iterator.foreach(println)
```

For instance, in the above we are bringing back all documents in the index, 50 results at a time, marshalled into
instances of `MyType` using the implicit `HitReader` (see the section on HitReaders). If you want just the raw
elasticsearch `Hit` object, then use `SearchIterator.hits`

Note: Whenever the results in a particular
batch have been iterated on, the `SearchIterator` will then execute another query for the next batch and block waiting on that query.
So if you are looking for a pure non blocking solution, consider the reactive streams implementation. However, if you just want a
quick and simple way to iterate over some data without bringing back all the results at once `SearchIterator` is perfect.





## Elastic Reactive Streams

Elastic4s has an implementation of the [reactive streams](http://www.reactive-streams.org) api for both publishing and subscribing that is built
using Akka. To use this, you need to add a dependency on the elastic4s-streams module.

There are two things you can do with the reactive streams implementation. You can create an elastic subscriber, and have that
stream data from some publisher into elasticsearch. Or you can create an elastic publisher and have documents streamed out to subscribers.

For full details read the [streams documentation](docs/streams.md)

## Using Elastic4s in your project

For gradle users, add (replace 2.12 with 2.13 for Scala 2.13):

```groovy
compile 'nl.gn0s1s:elastic4s-core_2.12:x.x.x'
```

For SBT users add:

```scala
libraryDependencies += "nl.gn0s1s" %% "elastic4s-core" % "x.x.x"
```

For Maven users add (replace 2.12 with 2.13 for Scala 2.13):

```xml
<dependency>
    <groupId>nl.gn0s1s/groupId>
    <artifactId>elastic4s-core_2.12</artifactId>
    <version>x.x.x</version>
</dependency>
```

Check for the latest released versions on [maven central](https://search.maven.org/search?q=g:nl.gn0s1s%20AND%20a:elastic4s*)

## Building and Testing

This project is built with SBT. So to build with:

```scala
sbt compile
```

And to test:

```scala
sbt test
```
The project is currently [cross-built](https://www.scala-sbt.org/1.x/docs/Cross-Build.html) against Scala 2.12, 2.13 and 3, when preparing a pull request the above commands should be run with the `sbt` `+` modifier to compile and test
against all versions. For example: `sbt +compile`.

For the tests to work you will need to run a local elastic instance on port 39227, _with security enabled_. One easy way of doing this is to use docker (via docker-compose):
`docker-compose up`

## Used By
* Barclays Bank
* HSBC
* Shazaam
* Lenses
* Iterable
* Graphflow
* Hotel Urbano
* Immobilien Scout
* Deutsche Bank
* Goldman Sachs
* HMRC
* Canal+
* AOE
* Starmind
* ShopRunner
* Soundcloud
* Rostelecom-Solar
* Shoprunner
* Twitter
* bluerootlabs.io
* mapp.com
* Jusbrasil
* wehkamp

_Raise a PR to add your company here_

![youkit logo](https://www.yourkit.com/images/yklogo.png) YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.

## Contributions
Contributions to elastic4s are always welcome. Good ways to contribute include:

* Raising bugs and feature requests
* Fixing bugs and enhancing the DSL
* Improving the performance of elastic4s
* Adding to the documentation

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013-2016 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
