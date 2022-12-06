package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import org.http4s.EntityEncoder
import org.http4s.Response
import org.http4s.Status
import org.http4s.dsl.impl.Statuses
import org.http4s.dsl.io._

/**
 * Extension methods for [[org.http4s]] objects.
 */
object Http4sExtension {

  /**
   * Rich functions for [[Status]].
   * @param x
   *   A code status
   */
  implicit class Http4sExtensionRichStatus(x: Status) extends Statuses {

    /**
     * From [[Status]] to an [[Response]] with [[api.dto.output]].
     * ([[https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#client_error_responses All status codes]])
     * @param dtoOut
     *   The output DTO (⚠ Some [[Status]] does not support DTO, in this case it will be ignored)
     * @param w
     *   Implicit JSON serializer
     * @tparam A
     *   Type of the output DTO
     * @return
     *   Response
     */
    def toResponseIOWithDtoOut[A](dtoOut: A)(implicit w: EntityEncoder[IO, A]): IO[Response[IO]] = x match {
      // Codes 1XX
      case Status.Continue           => Continue()
      case Status.SwitchingProtocols => SwitchingProtocols()
      case Status.Processing         => IO(Response(Processing))

      // Codes 2XX
      case Status.Ok                          => Ok(dtoOut)
      case Status.Created                     => Created(dtoOut)
      case Status.Accepted                    => Accepted(dtoOut)
      case Status.NonAuthoritativeInformation => NonAuthoritativeInformation(dtoOut)
      case Status.NoContent                   => NoContent()
      case Status.ResetContent                => ResetContent()
      case Status.PartialContent              => PartialContent(dtoOut)
      case Status.MultiStatus                 => MultiStatus(dtoOut)
      case Status.AlreadyReported             => AlreadyReported(dtoOut)
      case Status.IMUsed                      => IMUsed(dtoOut)

      // Codes 3XX
      case Status.MultipleChoices   => MultipleChoices(dtoOut)
      case Status.MovedPermanently  => MovedPermanently(dtoOut)
      case Status.Found             => Found(dtoOut)
      case Status.SeeOther          => SeeOther(dtoOut)
      case Status.NotModified       => NotModified()
      case Status.UseProxy          => IO(Response(UseProxy))
      case Status.TemporaryRedirect => TemporaryRedirect(dtoOut)
      case Status.PermanentRedirect => PermanentRedirect(dtoOut)

      // Codes 4XX
      case Status.BadRequest                  => BadRequest(dtoOut)
      case Status.Unauthorized                => IO(Response(Unauthorized))
      case Status.PaymentRequired             => PaymentRequired(dtoOut)
      case Status.Forbidden                   => Forbidden(dtoOut)
      case Status.NotFound                    => NotFound(dtoOut)
      case Status.MethodNotAllowed            => IO(Response(MethodNotAllowed))
      case Status.NotAcceptable               => NotAcceptable(dtoOut)
      case Status.ProxyAuthenticationRequired => ProxyAuthenticationRequired(dtoOut)
      case Status.RequestTimeout              => RequestTimeout(dtoOut)
      case Status.Conflict                    => Conflict(dtoOut)
      case Status.Gone                        => Gone(dtoOut)
      case Status.LengthRequired              => LengthRequired(dtoOut)
      case Status.PreconditionFailed          => PreconditionFailed(dtoOut)
      case Status.PayloadTooLarge             => PayloadTooLarge(dtoOut)
      case Status.UriTooLong                  => UriTooLong(dtoOut)
      case Status.UnsupportedMediaType        => UnsupportedMediaType(dtoOut)
      case Status.RangeNotSatisfiable         => RangeNotSatisfiable(dtoOut)
      case Status.ExpectationFailed           => ExpectationFailed(dtoOut)
      case Status.UnprocessableEntity         => UnprocessableEntity(dtoOut)
      case Status.Locked                      => Locked(dtoOut)
      case Status.FailedDependency            => FailedDependency(dtoOut)
      case Status.UpgradeRequired             => UpgradeRequired(dtoOut)
      case Status.PreconditionRequired        => PreconditionRequired(dtoOut)
      case Status.TooManyRequests             => TooManyRequests(dtoOut)
      case Status.RequestHeaderFieldsTooLarge => RequestHeaderFieldsTooLarge(dtoOut)
      case Status.UnavailableForLegalReasons  => UnavailableForLegalReasons(dtoOut)

      // Codes 5XX
      case Status.InternalServerError           => InternalServerError(dtoOut)
      case Status.NotImplemented                => NotImplemented(dtoOut)
      case Status.BadGateway                    => BadGateway(dtoOut)
      case Status.ServiceUnavailable            => ServiceUnavailable(dtoOut)
      case Status.GatewayTimeout                => GatewayTimeout(dtoOut)
      case Status.HttpVersionNotSupported       => HttpVersionNotSupported(dtoOut)
      case Status.VariantAlsoNegotiates         => VariantAlsoNegotiates(dtoOut)
      case Status.InsufficientStorage           => InsufficientStorage(dtoOut)
      case Status.LoopDetected                  => LoopDetected(dtoOut)
      case Status.NotExtended                   => NotExtended(dtoOut)
      case Status.NetworkAuthenticationRequired => NetworkAuthenticationRequired(dtoOut)

      // Else
      case _ => InternalServerError(dtoOut)
    }

  }

}
