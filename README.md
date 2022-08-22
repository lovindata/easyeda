# learning-http4s

# Logbook

- Implement authorization
  - When issue of not found session => The error 500 is not clear
  - Need to try to understand `def retrieveUser: Kleisli[IO, Long, User] = Kleisli(id => IO(???))`
  - cf look at doc [here](https://http4s.org/v0.23/docs/auth.html)
- Continue on DEV with status & counts
- Setup config files
- Setup Tapir Swagger