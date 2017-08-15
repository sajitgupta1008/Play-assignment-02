package controllers

import models._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import akka.stream.Materializer
import scala.concurrent.Future
import play.api.test.Helpers.{redirectLocation, _}

class UserProfileControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val userRepository: UserRepository = mock[UserRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userHobbiesRepository: UserHobbiesRepository = mock[UserHobbiesRepository]
  val forms: UserForms = mock[UserForms]
  val messages: MessagesApi = mock[MessagesApi]

  val mobileNo = 8743922586L
  val age = 22

  when(forms.profileForm).thenReturn(new UserForms().profileForm)
  val userProfileController: UserProfileController = new UserProfileController(userRepository, hobbiesRepository, userHobbiesRepository,
    forms, messages)
  "UserProfileController" should {

    "be able to log out" in {
      val result = userProfileController.logout().apply(FakeRequest("GET", "/"))
      redirectLocation(result) mustBe Some("/")
    }

    "be able to get profile details" in {
      when(userRepository.getUserDetails("sajit@gmail.com")).thenReturn(Future.successful(
        UserProfileData("sajit", None, "gupta", mobileNo, "male", age)))
      when(userHobbiesRepository.getUserHobbies("sajit@gmail.com")).thenReturn(Future.successful(
        List("cricket")))
      when(userRepository.isAdmin("sajit@gmail.com")).thenReturn(Future.successful(true))

      val result = userProfileController.getProfileDetails().apply(FakeRequest("GET", "/").
        withSession("email" -> "sajit@gmail.com"))
      status(result) mustBe 200
    }

    "be able to update profile details successfully" in {
      val updatedDetails: UserProfileData = UserProfileData("sajit", None, "gupta", mobileNo, "male", age)

      when(userRepository.updateUserDetails("sajit@gmail.com", updatedDetails)).thenReturn(
        Future.successful(true))
      when(userHobbiesRepository.updateHobbies("sajit@gmail.com", List("cricket"))).thenReturn(
        Future.successful(Some(age)))

      val result = userProfileController.updateDetails().apply(FakeRequest("GET", "/").
        withSession("email" -> "sajit@gmail.com").withFormUrlEncodedBody("name" -> "sajit",
        "middleName" -> "", "lastName" -> "gupta", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "22",
        "hobbies[0]" -> "cricket"))
      redirectLocation(result) mustBe Some("/getDetails")
    }

    "failed to update profile details" in {
      val updatedDetails: UserProfileData = UserProfileData("sajit", None, "gupta", mobileNo, "male", age)

      when(userRepository.updateUserDetails("sajit@gmail.com", updatedDetails)).thenReturn(
        Future.successful(false))
      when(userHobbiesRepository.updateHobbies("sajit@gmail.com", List("cricket"))).thenReturn(
        Future.successful(Some(age)))

      val result = userProfileController.updateDetails().apply(FakeRequest("GET", "/").
        withSession("email" -> "sajit@gmail.com").withFormUrlEncodedBody("name" -> "sajit",
        "middleName" -> "", "lastName" -> "gupta", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "22",
        "hobbies[0]" -> "cricket"))
      status(result) mustBe 500
    }

    "failed to update hobbies details but updated other details" in {
      val updatedDetails: UserProfileData = UserProfileData("sajit", None, "gupta", mobileNo, "male", age)

      when(userRepository.updateUserDetails("sajit@gmail.com", updatedDetails)).thenReturn(
        Future.successful(true))
      when(userHobbiesRepository.updateHobbies("sajit@gmail.com", List("cricket"))).thenReturn(
        Future.successful(Some(age - age)))

      val result = userProfileController.updateDetails().apply(FakeRequest("GET", "/").
        withSession("email" -> "sajit@gmail.com").withFormUrlEncodedBody("name" -> "sajit",
        "middleName" -> "", "lastName" -> "gupta", "mobileNo" -> "8743922586", "gender" -> "male", "age" -> "22",
        "hobbies[0]" -> "cricket"))
      status(result) mustBe 500
    }
  }
}

