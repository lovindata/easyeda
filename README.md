# EasyEDA - Easy exploratory data analysis

[![Generic badge](https://img.shields.io/badge/Scala-2.13-darkred.svg?style=plastic)](https://www.scala-lang.org/)
[![Generic badge](https://img.shields.io/badge/http4s-0.23-red.svg?style=plastic)](https://http4s.org/#)
[![Generic badge](https://img.shields.io/badge/Doobie-1.0-orangered.svg?style=plastic)](https://tpolecat.github.io/doobie/)
[![Generic badge](https://img.shields.io/badge/Spark-3.3-orange.svg?style=plastic)](https://spark.apache.org/)
[![Generic badge](https://img.shields.io/badge/SwaggerUI-4.14-green.svg?style=plastic)](https://swagger.io/)
[![Generic badge](https://img.shields.io/badge/SBT-1.7-blue.svg?style=plastic)](https://www.scala-sbt.org/)
[![Generic badge](https://img.shields.io/badge/OpenJDK-11-white.svg?style=plastic)](https://adoptium.net/)

![stackUsed](docs/stackUsed.png)

📊😌 **EasyEDA** is a REST API for doing common exploratory data analysis.
It is powered by Typelevel microservice stack **[http4s](https://http4s.org/v0.23/docs/quickstart.html)**
and **[Doobie](https://tpolecat.github.io/doobie/)** (with **[Cats IO](https://typelevel.org/cats-effect/)**)
plus **[Spark](https://spark.apache.org/docs/3.3.3/)**
providing fast asynchronous parallel request processing and **in-memory** data computation.
It means none of your source data are saved somewhere plus everything are done on the fly 🚀🤩!


# Want to contribute ? 😉

- [Stack used image](docs/stackUsed.png) can be directly modified via [draw.io](https://app.diagrams.net/)
- [IntelliJ IDEA CE](https://www.jetbrains.com/idea/) was used to build the source code part
- This [VSCode extension](https://marketplace.visualstudio.com/items?itemName=42Crunch.vscode-openapi) was used to edit the [OpenAPI documentation](src/main/resources/swagger/openapi.yaml)


# Logbook (🙏😣 Work still in progress...)

- Building whole execution of preview (😼 HERE AT THE MOMENT 😼)
  - Design properly the data saved in the DB for Jobs
- Migrate to Redis (😼 HERE AT THE MOMENT 😼)
  - Learn redis data types & [pseudo tables](https://stackoverflow.com/questions/16375188/redis-strings-vs-redis-hashes-to-represent-json-efficiency)
  - Start migration DEV setup
  - Setup AOF every sec mode
- Implement e2e exception handling with proper `EitherT[IO, Exception, A]` (😼 HERE AT THE MOMENT 😼)
- Think about cron jobs not terminated but still here sessions ?
  - K8S stateless compatibility too be-careful ⚠️
    - The implementation of "not-continue"
- Work on Logging (Spark logs annoying + No app logging)
- Continue DEV on basic statistics
- Start DEV on Spark NLP Service
  - Introduce the type `Sentence`


# Notes

- [Jedis JavaDoc](https://javadoc.io/doc/redis.clients/jedis/latest/index.html)
- [Redis commands](https://redis.io/commands/)