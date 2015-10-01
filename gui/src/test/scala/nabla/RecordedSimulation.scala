package nabla

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

  //val host = System.getProperty("perfHost")
  protected val host = "localhost"
  protected val port = "9090"

	val httpProtocol = http
			.baseURL("http://" + host + ":" + port)
			.inferHtmlResources()
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptEncodingHeader("gzip, deflate")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.connection("keep-alive")
			.userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")


	val headers_1 = Map(
			"Cache-Control" -> """max-age=0"""
	)

	val headers_2 = Map(
			"Content-Type" -> """application/x-www-form-urlencoded"""
	)

	val headers_3 = Map(
			"Accept" -> """text/css,*/*;q=0.1"""
	)

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/visma/loan.xhtml"))
		.pause(11)
		.exec(http("request_1")
			.post("/visma/loan.xhtml")
			.formParam("loan_form", "loan_form")
			.formParam("loan_form:loanType", "1")
			.formParam("loan_form:loanAmount", "100000")
			.formParam("loan_form:paybackTime", "10")
			.formParam("loan_form:payment", "Show monthlty payments")
			.formParam("javax.faces.ViewState", "5372695818220130002:-6817230658628640359"))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
