package controllers

import models.{HobbiesRepository, UserHobbiesRepository, UserRepository}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import akka.stream.Materializer
import scala.concurrent.Future
import play.api.test.Helpers._

class RegistrationControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val userRepository: UserRepository = mock[UserRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userHobbiesRepository: UserHobbiesRepository = mock[UserHobbiesRepository]
  val forms: UserForms = mock[UserForms]
  val messages: MessagesApi = mock[MessagesApi]

  when(forms.registerForm).thenReturn(new UserForms().registerForm)
  when(hobbiesRepository.getHobbies).thenReturn(Future.successful(List("cricket", "badminton", "cycling")))

  val registrationController: RegistrationController = new RegistrationController(userRepository, hobbiesRepository
    , userHobbiesRepository, forms, messages)

  "RegistrationController" should {
    "show registration form" in {
      val result = registrationController.showRegisterForm().apply(FakeRequest("GET", "/"))
      status(result) mustEqual 200
      /*
            val result = call(registrationController.showRegisterForm(), FakeRequest(GET, "/register"))

            status(result) mustBe OK
      */
    }
    "be able to get the details from registration form" in {
      when(userRepository.checkUserExists("sajit@gmail.com")).thenReturn(Future.successful(false))


    }

  }

}