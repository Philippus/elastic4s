package com.sksamuel.elastic4s.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.scaladsl.Flow

import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

private[akka] class DefaultHttpPoolFactory(settings: ConnectionPoolSettings, verifySslCertificate : Boolean)(
  implicit system: ActorSystem)
  extends HttpPoolFactory {

  private val http = Http()

  private val poolSettings = settings.withResponseEntitySubscriptionTimeout(
    Duration.Inf) // we guarantee to consume consume data from all responses

  // take from https://gist.github.com/iRevive/7d17144284a7a2227487635ec815860d
  private val trustfulSslContext: SSLContext = {
    object NoCheckX509TrustManager extends X509TrustManager {
      override def checkClientTrusted(chain: Array[X509Certificate], authType: String) = ()

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String) = ()

      override def getAcceptedIssuers = Array[X509Certificate]()
    }

    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
    context
  }

  // https://doc.akka.io/docs/akka-http/current/client-side/client-https-support.html#disabling-hostname-verification
  private val insecureConnectionContext = ConnectionContext.httpsClient {(host,port)=>
    val engine = trustfulSslContext.createSSLEngine(host,port)
    engine.setUseClientMode(true)
    engine
  }

  override def create[T]()
  : Flow[(HttpRequest, T), (HttpRequest, Try[HttpResponse], T), NotUsed] = {
    Flow[(HttpRequest, T)].map {
      case (request, state) => (request, (request, state))
    }.via{
      http.superPool[(HttpRequest, T)](
        settings = poolSettings,
        connectionContext = if(verifySslCertificate) http.defaultClientHttpsContext else insecureConnectionContext
      ).map {
        case (response, (request, state)) => (request, response, state)
      }
    }
  }

  override def shutdown(): Future[Unit] = http.shutdownAllConnectionPools()
}
