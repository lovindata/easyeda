# learning-http4s

# Logbook

- Building whole execution of preview
  - Re-think about routes & models logic on this part
  - WIP on Controller / IO logic between the `withBlabla` & `JobController`
  - Then convert String into DataFrame with [link](https://stackoverflow.com/questions/39111918/can-i-read-a-csv-represented-as-a-string-into-apache-spark-using-spark-csv)
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service

# Notes

- `_.withReceiveBufferSize(256 * 1024)` & `_.take(2)` both are related. The buffer size is exactly defining how many
elements you have in your `Stream[IO, String]`