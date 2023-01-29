# EasyEDA - Easy fast exploratory data analysis

[![Generic badge](https://img.shields.io/badge/Scala-2.13-darkred.svg?style=plastic)](https://www.scala-lang.org/)
[![Generic badge](https://img.shields.io/badge/CatsEffect-3.3-lightblue.svg?style=plastic)](https://typelevel.org/cats-effect/)
[![Generic badge](https://img.shields.io/badge/Http4s-0.23-red.svg?style=plastic)](https://http4s.org/#)
[![Generic badge](https://img.shields.io/badge/Spark-3.3-orange.svg?style=plastic)](https://spark.apache.org/)
[![Generic badge](https://img.shields.io/badge/SBT-1.7-blue.svg?style=plastic)](https://www.scala-sbt.org/)
[![Generic badge](https://img.shields.io/badge/OpenJDK-11-white.svg?style=plastic)](https://adoptium.net/)
[![Generic badge](https://img.shields.io/badge/Jedis-4.3-black.svg?style=plastic)](https://github.com/redis/jedis)
[![Generic badge](https://img.shields.io/badge/RedisStack-6.2-orangered.svg?style=plastic)](https://redis.io/docs/stack/)
[![Generic badge](https://img.shields.io/badge/SwaggerUI-4.14-green.svg?style=plastic)](https://swagger.io/)

![stackUsed](docs/stackUsed.png)

📊😌 **EasyEDA** is a REST API for doing common exploratory data analysis.
It is powered by Typelevel stack **[http4s](https://http4s.org/v0.23/docs/quickstart.html)** with
**[Cats Effect](https://typelevel.org/cats-effect/)**, **[Spark](https://spark.apache.org/docs/3.3.3/)**
and **[Redis](https://redis.io/)** providing fully vertically scaled parallel request processing
and extremely fast **in-memory** non-persistent data manipulation & computation.
It means none of your source data are saved somewhere and everything are done on the fly 🚀🤩!

# Want to contribute ? 😉

- [Stack used image](docs/stackUsed.png) can be directly modified via [draw.io](https://app.diagrams.net/)
- [IntelliJ IDEA CE](https://www.jetbrains.com/idea/) was used to build the source code
- This [VSCode extension](https://marketplace.visualstudio.com/items?itemName=42Crunch.vscode-openapi) was used to edit the [OpenAPI documentation](src/main/resources/swagger/openapi.yaml)

# Logbook (🙏😣 Work still in progress...)

- Setup database and start coding for EloData authentication (😼 HERE AT THE MOMENT 😼)
  - Give up email verification directly go like Snowflake style (SaaS)
  - Understand OAuth2 and see if it's applicable in your case
  - Solve compilation issue with doobie

- Finalize migration
  - Rework the already in-place UTs
    - Issue with combined AsyncFreeSpec + `withObjectMocked => Unit` (PAUSED)
  - Job will stay on "Running" it error handling need to be on overall `withJob` => Done need to test
- Think about cron jobs not terminated but still here sessions ?
  - K8S stateless compatibility too be-careful ⚠️
    - The implementation of "not-continue"
- Start learning ReactJs & FrontEnd dev
  - Continue "on all in-one container" with ReactApp served by HTTP4s / Tapir (😼 HERE AT THE MOMENT 😼)
- Start learning event streams & prepare async routes for Job monitoring
- Start trying Tapir for embedding routes
- Work on Logging (Spark logs annoying + No app logging)
- Continue DEV on basic statistics
- Start DEV on Spark NLP Service
  - Introduce the type `Sentence`
- SCoverage & CI/CD

# Notes

- [NoSQL Data modelling patterns & theories](https://redis.com/blog/nosql-data-modeling/#:~:text=What%20is%20a%20NoSQL%20data,how%20it%20all%20connects%20together.)
- [Jedis JavaDoc](https://javadoc.io/doc/redis.clients/jedis/latest/index.html)
- [Redis commands](https://redis.io/commands/)
