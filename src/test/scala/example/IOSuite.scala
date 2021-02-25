package example

import cats.effect.IO

class IOSuite extends munit.FunSuite {
  override def munitValueTransforms = super.munitValueTransforms ++ List(
    new ValueTransform("IO", {
      case io: IO[_] => io.unsafeToFuture()
    })
  )
}
