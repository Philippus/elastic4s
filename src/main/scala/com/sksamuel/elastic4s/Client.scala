package com.sksamuel.elastic4s

import scala.concurrent.Future

/** @author Stephen Samuel */
trait Client {
    def index(req: IndexReq): Future[IndexRes]
}
