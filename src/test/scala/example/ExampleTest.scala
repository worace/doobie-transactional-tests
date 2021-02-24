package example

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.{Transactor, Strategy}
import doobie.free.FC.{unit => DoNothing}
import cats.effect.IO
import scala.concurrent.ExecutionContext
import java.sql.Connection
import doobie._
import doobie.implicits._

class SandboxTest extends munit.FunSuite {
  implicit val contextShift = cats.effect.IO.contextShift(ExecutionContext.global)
  val blocker = cats.effect.Blocker.liftExecutionContext(doobie.util.ExecutionContexts.synchronous)
  override def munitValueTransforms = super.munitValueTransforms ++ List(
    new ValueTransform("IO", {
      case io: IO[_] => io.unsafeToFuture()
    })
  )

  def startPool: HikariDataSource = {
    val conf = new HikariConfig()
    conf.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource")
    conf.addDataSourceProperty("databaseName", "doobie_tests")
    conf.addDataSourceProperty("serverName", "localhost")
    conf.addDataSourceProperty("portNumber", 5432)
    conf.setUsername("postgres")
    conf.setPassword("postgres")
    new HikariDataSource(conf)
  }

  private var pool: HikariDataSource = null
  override def beforeAll(): Unit = {
    pool = startPool
  }

  override def afterAll(): Unit = {
    pool.close()
  }

  case class Context(conn: Connection, xa: Transactor[IO])
  val fixture = FunFixture[Context](
    setup = { test =>
      val conn = pool.getConnection()
      conn.setAutoCommit(false)
      val xa = Transactor.fromConnection[IO](conn, blocker)
      val rollbackXa = Transactor.strategy.set(xa, Strategy.default.copy(after = DoNothing, oops = DoNothing, always = DoNothing))
      Context(conn, rollbackXa)
    },
    teardown = { ctx =>
      ctx.conn.rollback
      if (!ctx.conn.isClosed) {
        ctx.conn.close()
      }
    }
  )
}


class ExampleTest extends SandboxTest {
  fixture.test("transactional test") { ctx: Context =>
    val cio = for {
      _ <- sql"insert into foods (name) values ('pizza')".update.run.transact(ctx.xa)
      names <- sql"select name from foods".query[String].to[Vector].transact(ctx.xa)
    } yield {
      assertEquals(names, Vector("pizza"))
    }
  }

  fixture.test("clean slate test") { ctx: Context =>
    val cio = for {
      names <- sql"select name from foods".query[String].to[Vector].transact(ctx.xa)
    } yield {
      assertEquals(names, Vector())
    }
  }
}
