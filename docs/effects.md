## Effect Types

TODO: fix this section

By default, elastic4s uses scala `Future`s when returning responses, but any effect type can be supported.

Internally, elastic4s uses two typeclasses for execution. An `Executor` which will wrap the result in an effect.

```scala
trait Executor[F[_]] {
  def exec(client: HttpClient, request: ElasticRequest): F[HttpResponse]
}
```

And `Functor` which is used to map effects.

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

The default `Executor` uses scala `Future`s to execute requests, but there are alternate Executors that can be used by
adding appropriate imports. The imports will create an implicit `Executor[F]` and a `Functor[F]`,
where `F` is some effect type.

### Cats-Effect IO
`import com.sksamuel.elastic4s.cats.effect.instances._` will provide implicit instances for `cats.effect.IO`

### Monix Task
`import com.sksamuel.elastic4s.monix.instances._` will provide implicit instances for `monix.eval.Task`

### Scalaz Task
`import com.sksamuel.elastic4s.scalaz.instances._` will provide implicit instances for `scalaz.concurrent.Task`

### ZIO Task
`import com.sksamuel.elastic4s.zio.instances._` will provide implicit instances for `zio.Task`
