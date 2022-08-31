# learning-http4s

# Logbook

- Building Multipart/form-data to string
  - Succeeded to receive a form data => Now need to parse it
    - Need to test "256 * 1024" default buffer size VS after the decoded into UT8 take(1)
  - ~~You need to interrupt the fs2.Stream when you reach a certain amount of octets~~
    - No, just let it go & Ember server have limiters
  - Then convert String into DataFrame with [link](https://stackoverflow.com/questions/39111918/can-i-read-a-csv-represented-as-a-string-into-apache-spark-using-spark-csv)
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service