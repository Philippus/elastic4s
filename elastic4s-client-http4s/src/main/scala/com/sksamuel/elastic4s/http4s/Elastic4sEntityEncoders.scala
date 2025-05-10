package com.sksamuel.elastic4s.http4s

import cats.effect.Sync
import com.sksamuel.elastic4s
import fs2.io.file.{Files, Path}
import org.http4s

import java.io.InputStream

trait Elastic4sEntityEncoders {

  implicit def elasticEntityEncoder[F[_]: Sync: Files]: http4s.EntityEncoder[F, elastic4s.HttpEntity] =
    new http4s.EntityEncoder[F, elastic4s.HttpEntity] {
      override def toEntity(a: elastic4s.HttpEntity): http4s.Entity[F] = {
        a match {
          case elastic4s.HttpEntity.StringEntity(str, _)     =>
            http4s.EntityEncoder.stringEncoder[F].toEntity(str)
          case elastic4s.HttpEntity.InputStreamEntity(is, _) =>
            http4s.EntityEncoder.inputStreamEncoder[F, InputStream].toEntity(Sync[F].pure(is))
          case elastic4s.HttpEntity.FileEntity(file, _)      =>
            http4s.EntityEncoder.pathEncoder[F].toEntity(Path.fromNioPath(file.toPath))
          case elastic4s.HttpEntity.ByteArrayEntity(arr, _)  =>
            http4s.EntityEncoder.byteArrayEncoder[F].toEntity(arr)
        }
      }

      override def headers: http4s.Headers = http4s.Headers.empty
    }

  implicit def optionalEntityEncoder[F[_], A](implicit
      ee: http4s.EntityEncoder[F, A]
  ): http4s.EntityEncoder[F, Option[A]] =
    new http4s.EntityEncoder[F, Option[A]] {
      override def toEntity(a: Option[A]): http4s.Entity[F] = {
        a.fold[http4s.Entity[F]](http4s.Entity.empty)(ee.toEntity)
      }

      override def headers: http4s.Headers = http4s.Headers.empty
    }

}
