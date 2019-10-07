
import io.gatling.core.Predef
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.ConstantRateOpenInjection
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SuggestionsSimulator extends Simulation {

  val url: String = System.getProperty("url", "http://localhost:9091")

  val loadTestingProfile = incrementConcurrentUsers(50)
    .times(100)
    .eachLevelLasting(5 seconds)
    .startingFrom(100)

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
      http("delayedService").get("/sleep/100")
    )

  setUp(scn
    .inject(loadTestingProfile)
    .protocols(httpConfig())
  )

}
