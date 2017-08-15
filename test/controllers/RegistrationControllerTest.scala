package controllers

import models.{HobbiesRepository, UserData, UserHobbiesRepository, UserRepository}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import akka.stream.Materializer
import play.api.mvc.Result
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
      status(result) mustEqual OK
    }

    "be able to save signup details from registration form" in {
      when(userRepository.checkUserExists("sajit@gmail.com")).thenReturn(Future.successful(false))
      val mobileNo = 8743922586L
      val age = 23
      val user: UserData = UserData(0, "sajit", None, "gupta", "sajit@gmail.com",
        "qwerty123", mobileNo, "male", age)
      when(userRepository.addUser(user)).thenReturn(Future.successful(true))
      when(userHobbiesRepository.addHobbies(user.userName, List("reading"))).thenReturn(Future.successful(Some(age)))

      val result: Future[Result] = registrationController.handleRegister().apply(FakeRequest("GET", "/handleregister").withFormUrlEncodedBody(
        "name" -> "sajit", "middleName" -> "", "lastName" -> "gupta", "userName" -> "sajit@gmail.com", "password" -> "qwerty123",
        "re_enterPassword" -> "qwerty123", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "23",
        "hobbies[0]" -> "reading"))

      redirectLocation(result) mustBe Some("/getDetails")
    }

    "fail to save user details" in {
      when(userRepository.checkUserExists("sajit@gmail.com")).thenReturn(Future.successful(false))
      val mobileNo = 8743922586L
      val age = 23
      val user: UserData = UserData(0, "sajit", None, "gupta", "sajit@gmail.com",
        "qwerty123", mobileNo, "male", age)
      when(userRepository.addUser(user)).thenReturn(Future.successful(false))

       val result: Future[Result] = registrationController.handleRegister().apply(FakeRequest("GET", "/handleregister").withFormUrlEncodedBody(
        "name" -> "sajit", "middleName" -> "", "lastName" -> "gupta", "userName" -> "sajit@gmail.com", "password" -> "qwerty123",
        "re_enterPassword" -> "qwerty123", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "23",
        "hobbies[0]" -> "reading"))

      status(result) mustBe 500
    }

    "handle the case where username already exists" in {
      when(userRepository.checkUserExists("sajit@gmail.com")).thenReturn(Future.successful(true))

      val result: Future[Result] = registrationController.handleRegister().apply(FakeRequest("GET", "/handleregister").withFormUrlEncodedBody(
        "name" -> "sajit", "middleName" -> "", "lastName" -> "gupta", "userName" -> "sajit@gmail.com", "password" -> "qwerty123",
        "re_enterPassword" -> "qwerty123", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "23",
        "hobbies[0]" -> "reading"))

      redirectLocation(result) mustBe Some("/login")
    }

    "handle bad request in registration form" in {
      val result: Future[Result] = registrationController.handleRegister().apply(FakeRequest("GET", "/handleregister").withFormUrlEncodedBody(
        "name" -> "sajit", "middleName" -> "", "lastName" -> "gupta", "userName" -> "sajit@gmail.com", "password" -> "qwerty123",
        "re_enterPassword" -> "qwerty123f", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "20",
        "hobbies[0]" -> "reading"))

      status(result) mustBe 400
    }
  }
}