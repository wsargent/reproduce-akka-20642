import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Sink
import org.asynchttpclient.{ RequestBuilderBase, SignatureCalculator }
import play.api.http.Port
import play.api.libs.json.JsString
import play.api.libs.oauth._
import play.api.mvc._
import play.api.test._
import play.core.server.Server
import play.api.mvc.Results.Ok
import play.api.libs.streams.Accumulator
import play.api.libs.ws.StreamedBody
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.mvc.Http

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

   "WS server" should {

    "streaming a request body" in withEchoServer { ws =>
      val source = Source(List("a", "b", "c").map(ByteString.apply))
      val res = ws.url("/post").withMethod("POST").withBody(StreamedBody(source))
        .withRequestFilter(AhcCurlRequestLogger())
        .execute()
      val body = await(res).body

      body must be("abc")
    }

   }

    def withEchoServer[T](block: play.api.libs.ws.WSClient => T) = {
      def echo = BodyParser { req =>
        import play.api.libs.concurrent.Execution.Implicits.defaultContext
        Accumulator.source[ByteString].mapFuture { source =>
          Future.successful(source).map(Right.apply)
        }
      }

      Server.withRouter() {
        case _ => Action(echo) { req =>
          Ok.chunked(req.body)
        }
      } { implicit port =>
        WsTestClient.withClient(block)
      }
    }
}
