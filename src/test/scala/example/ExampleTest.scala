package example

import doobie._
import doobie.implicits._

class TransactionalExampleTest extends SandboxTest {
  fixture.test("transactional test") { ctx: Context =>
    for {
      _ <- sql"insert into foods (name) values ('pizza')".update.run.transact(ctx.xa)
      names <- sql"select name from foods".query[String].to[Vector].transact(ctx.xa)
    } yield {
      assertEquals(names, Vector("pizza"))
    }
  }

  (0 to 100).map { i =>
    fixture.test(s"transactional test $i") { ctx: Context =>
      for {
        _ <- sql"insert into foods (name) values ('pizza')".update.run.transact(ctx.xa)
        names <- sql"select name from foods".query[String].to[Vector].transact(ctx.xa)
      } yield {
        assertEquals(names, Vector("pizza"))
      }
    }
  }
}
