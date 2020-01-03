
import io.gatling.core.Predef
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.ConstantRateOpenInjection
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class SuggestionsSimulator extends Simulation {

  val url: String = System.getProperty("url", "http://localhost:8080")

  val loadTestingProfile = constantConcurrentUsers(100) during (300 seconds)

  def httpConfig() = http
    .baseUrl(url)
    .doNotTrackHeader("1")
    .disableCaching
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/35.0")
    .shareConnections


  val scn: ScenarioBuilder = scenario("Suggestion API")
    .exec(
      http("delayedService").get("/plaintext")
    )

  setUp(scn
    .inject(loadTestingProfile)
    .protocols(httpConfig())
  )

}
