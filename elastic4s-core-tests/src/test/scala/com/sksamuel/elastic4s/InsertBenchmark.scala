package com.sksamuel.elastic4s

import java.util.concurrent.Executors
import java.util.{ Date, UUID }
import com.sksamuel.elastic4s.testkit.ElasticSugar

import org.scalatest.{ Matchers, FunSuite }

import scala.util.Random

class InsertBenchmark extends FunSuite with ElasticSugar with Matchers {

  import scala.concurrent.duration._

  val n = 75000
  val b = 100

  val sampleBody =
    """
      |There is "no doubt" a prize-winning Irish setter was "maliciously poisoned" at Crufts, one of his owners has said.
      |Thendara Satisfaction, known as Jagger, died after leaving the Birmingham show. His owners say he ate poisoned beef.
      |Co-owner Jeremy Bott said he did not think the dog was targeted, but the culprit may have been acting on "a grudge against dogs or the Crufts show".
      |The Kennel Club said it was the only case of poisoning being investigated.
      |Fellow owner of Jagger, Aleksandra Lauwers, said they had lost "our love, family member and best friend to our son".
      |Mr Bott's wife, Dee Milligan-Bott, told BBC Radio 5 live: "I don't believe in my heart of hearts that this was another competitor or anyone involved in the dog world.
      |"I can only imagine that it was a random act that somebody premeditated and wanted to cause total distress at the best dog show in the world.
      |"It's not unknown for people to do things like this.
      |"He was a typical Irish setter, totally trustworthy and so loved. We are devastated.
      |Jagger had just won second place in his category
      |"The Crufts committee and all championship show dog committees will have to look at security."
      |Jagger came second in his class at the show at Birmingham's NEC on Thursday.
      |He died after returning to Belgium with Ms Lauwers.
      |Leicester-based breeder Mrs Milligan-Bott claimed the dog must have been given the meat "while on his bench at Crufts", calling it "a heinous crime."
      |Jump media playerMedia player helpOut of media player. Press enter to return or tab to continue.
      |Speaking to BBC Radio 4, her husband Jeremy said: "When the vet opened up his stomach, she found cubes of meat - some sort of beef-like steak - and they had been sewn up with poison inside.
      |"She thinks there were possibly two or three types of poison.
      |"I think she identified one as a slug killer. I would guess that the other would turn out to be a rat poison or some industrial type of poison."
      |He said he did not believe the attack was targeted but he did not think the culprit would be caught.
      |He said: "They will hopefully try with the CCTV they have in the halls at Crufts but I don't think they will be able to find anybody."
      |Irish Setters competing at Crufts
      |Jagger's owner, Jeremy Bott, said the NEC would check its CCTV but he did not believe the culprit would be caught
      |The couple's daughter, Amy Nettleton, said: "The accessibility of shows such as Crufts... is such that the general public can wander in and out of the dogs' benches and approach any dog, so to keep an eye on everybody who came up and spoke to the dogs is very difficult."
      |Ms Nettleton described her family as "just devastated and beside themselves".
      |"The sensationalisation that the media has portrayed today - that Jagger was worth £50,000 - is beyond ridiculous," she said.
      |"Jagger, to his family, was priceless and he was also used, not only as a family pet but as pet therapy."
      |She said his owners took him into elderly care homes.
      |"He would sit there and give the residents some delight in him just being around."
      |The Kennel Club said it was awaiting a toxicology report from Belgian police. It said it had not received any other reports of sickness in dogs at Crufts.
    """.stripMargin

  ignore("benchmarking insertion of n documents") {

    val executor = Executors.newFixedThreadPool(8)
    val start = System.currentTimeMillis
    for (k <- 0 until n / b) {
      executor.submit(new Runnable {
        override def run(): Unit = {
          val articles = List.fill(b)(article)
          val op = articles.map(article => {
            index into "benchmark" / "articles" fields (
              "title" -> article.title,
              "body" -> article.body,
              "date" -> article.date.toString
            )
          })
          client.execute {
            bulk(op)
          }.await
          println(s"Inserted batch $k")
        }
      })
    }
    blockUntilCount(n, "benchmark", "articles")
    val duration = (System.currentTimeMillis - start).millis
    println(s"Insertion of $n records took ${duration.toSeconds}s")

    client.execute {
      search in "benchmark" / "articles" query "toxicology"
    }.await.getHits.totalHits shouldBe n
  }

  def randomTitle = "some article " + UUID.randomUUID.toString
  def article = Article(randomTitle, Random.shuffle(sampleBody.split("\\s").toList).mkString(" "), new Date)
}

case class Article(title: String, body: String, date: Date)

