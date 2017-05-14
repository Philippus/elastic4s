package controllers

import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import org.elasticsearch.common.settings.Settings
import play.api.mvc.{Action, Controller}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.searches.{RichSearchResponse, SearchDefinition}
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket
import org.elasticsearch.search.aggregations.metrics.max.InternalMax

import scala.concurrent._
import ExecutionContext.Implicits.global

object Elastic4sController extends Controller {

  lazy val settings = Settings.builder().put("cluster.name", "localcluster").build()
  // This is where code throws exception.
  lazy val esClient = TcpClient.transport(settings, ElasticsearchClientUri("elasticsearch://localhost:9300"))

  // Debugger never reaches here
  def aggregationTest = Action.async {
    esClient.execute {
      search("bank")
        .size(0)
        .aggregations(sumAgg("agebalance", "balance"))
    }.map {
      (response: RichSearchResponse) =>
        // Do something
        println(response.ids)
        Ok(views.html.index("successful"))
    }
  }

}
