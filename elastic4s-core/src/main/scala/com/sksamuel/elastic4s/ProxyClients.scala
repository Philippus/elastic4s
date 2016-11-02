package com.sksamuel.elastic4s

import java.lang.reflect.{ InvocationHandler, Method }

import org.elasticsearch.client.{ ClusterAdminClient, IndicesAdminClient, Client }

/** @author Stephen Samuel */
object ProxyClients {

  lazy val cluster: ClusterAdminClient = proxy[ClusterAdminClient]
  lazy val client: Client = proxy[Client]
  lazy val indices: IndicesAdminClient = proxy[IndicesAdminClient]

  @SuppressWarnings(Array("all"))
  private def proxy[T: Manifest] = java.lang.reflect.Proxy.newProxyInstance(
    getClass.getClassLoader,
    Array[Class[_]](manifest.runtimeClass.asInstanceOf[Class[T]]),
    new InvocationHandler {
      override def invoke(proxy: scala.Any, method: Method, args: Array[AnyRef]): AnyRef = null
    }
  ).asInstanceOf[T]

}
