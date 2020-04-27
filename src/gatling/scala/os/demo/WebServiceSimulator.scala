
import io.gatling.core.Predef
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.ConstantRateOpenInjection
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class PerformanceTest extends Simulation {

  /**
   * User can specify different service location with additional JAVA_OPTS
   * e.g.: JAVA_OPTS="-Durl=http://host:port -Dusers=1000 -Dduration=8000"
   */
  val url: String = System.getProperty("url", "http://zmachine:8080")
  val nbUsers = Integer.getInteger("users", 500)
  val duration = Integer.getInteger("duration", 300)

  val loadTestingProfile = constantConcurrentUsers(nbUsers) during (duration seconds)

  def httpConfig() = http
    .baseUrl(url)
    .doNotTrackHeader("1")
    .disableCaching
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/35.0")
    .shareConnections


  val scn: ScenarioBuilder = scenario("Basic Performance Test")
    .exec(
      http("ping").get("/plaintext")
    )

  setUp(scn
    .inject(loadTestingProfile)
    .protocols(httpConfig())
  )

}
