## Transactional Tests with Doobie

### Setup

* Have Postgres running locally on 5432
* Create DB: `createdb doobie_tests`
* `psql -d doobie_tests -c "create table foods (name text);"`
* `sbt test`
* `psql -d doobie_tests -c "select * from foods;"` -- look ma no rows
