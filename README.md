# learning-http4s

# Logbook

- Build the ER diagram
  - Your objective auto-EDA for NLP & Tabular data
  - Add auto created_at, updated_at to Job then Session
- Building Multipart/form-data to string
  - Succeeded to receive a form data => Now need to parse it
    - No, just let it go & Ember server have limiters
  - Then convert String into DataFrame with [link](https://stackoverflow.com/questions/39111918/can-i-read-a-csv-represented-as-a-string-into-apache-spark-using-spark-csv)
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service

# Notes

- `_.withReceiveBufferSize(256 * 1024)` & `_.take(2)` both are related. The buffer size is exactly defining how many
elements you have in your `Stream[IO, String]`