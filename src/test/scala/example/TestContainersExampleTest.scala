package example

import doobie.util.transactor.Transactor
import cats.effect.{IO, Blocker}
import scala.concurrent.ExecutionContext
import doobie._
import doobie.implicits._
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForEach
import munit.CatsEffectSuite

class TestContainersExampleTest extends CatsEffectSuite with TestContainerForEach {
  override val containerDef = PostgreSQLContainer.Def()

  def connect(container: PostgreSQLContainer): Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      container.jdbcUrl,
      container.username,
      container.password,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )
  }

  (0 to 5).map { i =>
    test(s"test postgres with testcontainer $i") {
      withContainers { postgresContainer =>
        val xa = connect(postgresContainer)
        assert(postgresContainer.jdbcUrl.nonEmpty)
        for {
          _ <- sql"create table foods (name text)".update.run.transact(xa)
          _ <- sql"insert into foods (name) values ('pizza')".update.run.transact(xa)
          names <- sql"select name from foods".query[String].to[Vector].transact(xa)
        } yield {
          assertEquals(names, Vector("pizza"))
        }
      }
    }
  }

}
