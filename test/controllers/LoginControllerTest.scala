package controllers

import akka.stream.Materializer
import models.{HobbiesRepository, UserHobbiesRepository, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi


class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite{

  implicit lazy val materializer: Materializer = app.materializer
  val userRepository: UserRepository = mock[UserRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userHobbiesRepository: UserHobbiesRepository = mock[UserHobbiesRepository]
  val forms: UserForms = mock[UserForms]
  val messages: MessagesApi = mock[MessagesApi]

}
