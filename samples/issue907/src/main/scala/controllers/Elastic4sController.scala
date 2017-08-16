package controllers

import com.sksamuel.elastic4s.searches.RichSearchResponse
import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import org.elasticsearch.common.settings.Settings
import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Elastic4sController extends App {

  val settings = Settings.builder().put("cluster.name", "elasticsearch").build()
  // This is where code throws exception.
  val esClient = TcpClient.transport(settings, ElasticsearchClientUri("elasticsearch://localhost:9300"))

  def aggregationTest: Future[Unit] = {
    esClient.execute {
      search("bank")
        .size(0)
        .aggregations(sumAgg("agebalance", "balance"))
    }.map {
      (response: RichSearchResponse) =>
        // Do something
        println(response.ids)
        println("ok")
    }
  }

  Await.ready(aggregationTest, Duration.Inf)

}
