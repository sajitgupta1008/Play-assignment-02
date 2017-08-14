package controllers

import com.google.inject.Inject
import models.{HobbiesRepository, UserData, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class User(name: String, middleName: Option[String], lastName: String, userName: String, password: String,
                re_enterPassword: String, mobileNo: Long, gender: String, age: Int, hobbies: Hobbies)

class RegistrationController @Inject()(userRepository: UserRepository, hobbyRepository: HobbiesRepository, forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def showRegisterForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.register(forms.registerForm))
  }

  def handleRegister(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    forms.registerForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("error occurred" + formWithErrors)
        Future.successful(BadRequest(views.html.register(formWithErrors)))
      },
      userData => {

        userRepository.checkUserExists(userData.userName) flatMap {
          case true => Future.successful(Redirect(routes.LoginController.showLoginForm())
            .flashing("exists" -> "Username already exists. Please sign in."))
          case false =>
            val user: UserData = UserData(0, userData.name, userData.middleName, userData.lastName, userData.userName,
              userData.password, userData.mobileNo, userData.gender, userData.age)

            userRepository.addUser(user).flatMap {
              case true => hobbyRepository.addHobbies(userData.userName, userData.hobbies).map {
                case true => Ok(views.html.displayRegisterData(userData))
                case false => Ok("failed to add hobbies")
              }

              case false => Future.successful(Ok("error"))
            }
        }

      })

  }
}


