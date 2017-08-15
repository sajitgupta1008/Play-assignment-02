package controllers

import models._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import akka.stream.Materializer
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import scala.concurrent.Future
import play.api.test.Helpers.{redirectLocation, _}

class AdminControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val userRepository: UserRepository = mock[UserRepository]
  val assignmentRepository: AssignmentRepository = mock[AssignmentRepository]
  val forms: UserForms = mock[UserForms]
  val messages: MessagesApi = mock[MessagesApi]
  when(forms.assignmentForm).thenReturn(new UserForms().assignmentForm)

  val adminController = new AdminController(userRepository, assignmentRepository, forms, messages)

  "AdminController" should {

    "show assignments " in {
      val result = adminController.showAssignmentForm().apply(FakeRequest("GET", "/"))
      status(result) mustEqual OK
    }

    "Add assignments with success " in {
      when(assignmentRepository.addAssignment(AssignmentData(0, "title", "desc"))).thenReturn(Future.successful(true))
      val result = adminController.addAssignment().apply(FakeRequest("GET", "/").withFormUrlEncodedBody("title" -> "title"
        , "description" -> "desc"))
      redirectLocation(result) mustBe Some("/showassignment")
    }

    "fail to add assignments" in {
      when(assignmentRepository.addAssignment(AssignmentData(0, "title", "desc"))).thenReturn(Future.successful(false))
      val result = adminController.addAssignment().apply(FakeRequest("GET", "/").withFormUrlEncodedBody("title" -> "title"
        , "description" -> "desc"))
      status(result) mustBe 500
    }

    "be able to view assignments" in {
      when(assignmentRepository.getAssignments()).thenReturn(Future.successful(List(AssignmentData(0, "title", "desc"))))
      val fakeRequest: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest("GET", "/").withSession(
        "email" -> "sajit@gmail.com").withFormUrlEncodedBody("title" -> "title", "description" -> "desc")
      when(userRepository.isAdmin("sajit@gmail.com")).thenReturn(Future.successful(true))
      val result: Future[Result] = adminController.viewAssignments().apply(fakeRequest)
      status(result) mustBe 200
    }

    "be able to delete assignment with success" in {
      when(assignmentRepository.deleteAssignment(0)).thenReturn(Future.successful(true))
      val result: Future[Result] = adminController.deleteAssignment(0).apply(FakeRequest("GET", "/"))
      redirectLocation(result) mustBe Some("/viewassignment")
    }

    "failed to delete assignment " in {
      when(assignmentRepository.deleteAssignment(0)).thenReturn(Future.successful(false))
      val result: Future[Result] = adminController.deleteAssignment(0).apply(FakeRequest("GET", "/"))
      status(result) mustBe 500
    }

    "be able to view users " in {
      when(userRepository.getAllUsers).thenReturn(Future.successful(List(UserData(0, "", None, "", "", "", 3L, "", 3))))
      val result: Future[Result] = adminController.viewUsers().apply(FakeRequest("GET", "/").withSession("email" -> "sajit@gmail.com"))
      status(result) mustBe 200
    }

    "be able to enable or disable users with success" in {
      when(userRepository.enableOrDisableUser("sajit@gmail.com", value = true)).thenReturn(Future.successful(true))
      val result: Future[Result] = adminController.enableOrDisableUser("sajit@gmail.com", isEnabled = false)
        .apply(FakeRequest("GET", "/"))
      redirectLocation(result) mustBe Some("/viewusers")
    }
    "fail to enable or disable users with success" in {
      when(userRepository.enableOrDisableUser("sajit@gmail.com", value = true)).thenReturn(Future.successful(false))
      val result: Future[Result] = adminController.enableOrDisableUser("sajit@gmail.com", isEnabled = false)
        .apply(FakeRequest("GET", "/"))
      status(result) mustBe 500
    }
  }
}
