# learning-http4s

# Logbook

- Building whole execution of preview
  - WIP on first testable Job implementation without DB
  - Just test it with basic Session DB
  - Job will be launched without saving to DBs for the moment
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service

# Notes

- `_.withReceiveBufferSize(256 * 1024)` & `_.take(2)` both are related. The buffer size is exactly defining how many
elements you have in your `Stream[IO, String]`
- User will import everytime its file
  - because don't want to save its data
  - better to not have customParams because not analyzable via SQL or through auto framework