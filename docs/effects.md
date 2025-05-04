## Effect Types

Internally, elastic4s uses `cats.Functor` typeclass to map effects:

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}
```

elastic4s provides instances for the following effect types:

### Scala Future's

To work with `scala.concurrent.Future`, you need to import the following:

```scala
import cats.implicits.catsStdInstancesForFuture
import scala.concurrent.ExecutionContext.Implicits.global // or define your own ExecutionContext
```

### Monix Task
`import com.sksamuel.elastic4s.monix.instances._` will provide implicit instances for `monix.eval.Task`

### Scalaz Task
`import com.sksamuel.elastic4s.scalaz.instances._` will provide implicit instances for `scalaz.concurrent.Task`

### ZIO Task
`import com.sksamuel.elastic4s.zio.instances._` will provide implicit instances for `zio.Task`.

Alternatively, you can use an official [zio-interop-cats](https://zio.dev/guides/interop/with-cats-effect/) library,
so then `import zio.interop.catz._` will bring necessary typeclass implementations.
