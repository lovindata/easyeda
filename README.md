# learning-http4s

# Logbook

- Building Multipart/form-data to string
  - Issue when receiving file + json at the same time (json cannot be encoded as JSON only string)
    - Which better choice ? ⚠️⁉
      - saving files on db then do all one by one routes ?
      - custom params with files no save but only results ?
  - Succeeded to receive a form data => Now need to parse it
    - No, just let it go & Ember server have limiters
  - Then convert String into DataFrame with [link](https://stackoverflow.com/questions/39111918/can-i-read-a-csv-represented-as-a-string-into-apache-spark-using-spark-csv)
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service

# Notes

- `_.withReceiveBufferSize(256 * 1024)` & `_.take(2)` both are related. The buffer size is exactly defining how many
elements you have in your `Stream[IO, String]`