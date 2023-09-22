package gatling

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class MySimulation extends Simulation {
	val httpProtocol: HttpProtocolBuilder = http
		.baseUrl("http://localhost:8080")
		.acceptHeader("application/json")
		.contentTypeHeader("application/json")

	val healthcheckScenario: ScenarioBuilder = scenario("Healthcheck")
		.exec(http("Healthcheck Request")
			.get("/api/v1/healthcheck")
			.check(status.is(200)))

	val registrationScenario: ScenarioBuilder = scenario("Registration")
		.exec(http("Registration Request")
			.post("/api/v1/auth/register")
			.body(StringBody(
				"""{
           "email": "mrvirtus3@gmail.com",
           "password": "123456789",
           "username": "Ryan Gosling"
        }"""
			))
			.asJson
			.check(status.is(200)))

	val authenticationScenario: ScenarioBuilder = scenario("Authentication")
		.exec(http("Authentication Request")
			.post("/api/v1/auth/authenticate")
			.body(StringBody(
				"""{
           "email": "papzan@example.com",
           "password": "password4"
        }"""
			))
			.asJson
			.check(status.is(200)))

	setUp(
		healthcheckScenario.inject(atOnceUsers(1)),
		registrationScenario.inject(rampUsers(2).during(5 seconds)),
		authenticationScenario.inject(atOnceUsers(1))
	).protocols(httpProtocol)
}
