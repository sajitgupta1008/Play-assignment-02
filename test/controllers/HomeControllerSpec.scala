package controllers

import akka.stream.Materializer
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._


class HomeControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {
  implicit lazy val materializer: Materializer = app.materializer

  val homeController: HomeController = new HomeController()
  "HomeController GET" should {

    "render the welcome page" in {
      val result = homeController.index().apply(FakeRequest("GET", "/"))
      status(result) mustBe 200
    }

    "render the register button" in {
      val result = homeController.registerAction().apply(FakeRequest("GET", "/"))
      redirectLocation(result) mustBe Some("/register")
    }
    "render the login button" in {
      val result = homeController.loginAction().apply(FakeRequest("GET", "/"))
      redirectLocation(result) mustBe Some("/login")
    }
  }
}
