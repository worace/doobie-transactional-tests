## Transactional Sandbox Tests with Doobie

[Blog Post](https://worace.works/2021/02/25/transactional-sandbox-testing-with-doobie/)

### Setup

* Have Postgres running locally on 5432
* Create DB: `createdb doobie_tests`
* `psql -d doobie_tests -c "create table foods (name text);"`
* `sbt test`
* `psql -d doobie_tests -c "select * from foods;"` -- look ma no rows
