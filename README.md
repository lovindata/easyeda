# learning-http4s

# Logbook

- Building Multipart/form-data to string
  - You need to interrupt the fs2.Stream when you reach a certain amount of octets
  - Then convert String into DataFrame with [link](https://stackoverflow.com/questions/39111918/can-i-read-a-csv-represented-as-a-string-into-apache-spark-using-spark-csv)
- Think about cron jobs not terminated but still here sessions ?
- Setup config files
- Start DEV on Spark NLP Service