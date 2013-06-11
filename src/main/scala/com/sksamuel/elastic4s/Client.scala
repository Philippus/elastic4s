package com.sksamuel.elastic4s

import scala.concurrent.Future
import org.elasticsearch.node.NodeBuilder._

/** @author Stephen Samuel */
class Client(val client: org.elasticsearch.client.Client) {
    def index(req: IndexReq): Future[IndexRes] = null
}

object Client {
    def local: Client = new Client(nodeBuilder.local(true).data(true).node.client)
}
