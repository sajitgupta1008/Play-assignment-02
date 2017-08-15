package controllers

import com.google.inject.Inject
import models.UserRepository
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class LoginUser(username: String, password: String)

class LoginController @Inject()(userRepository: UserRepository, forms: UserForms, implicit val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def showLoginForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(forms.loginForm))
  }

  def showForgetForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.forgotPassword(forms.forgetPasswordForm))
  }

  def handleLogin(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    forms.loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Error occurred " + formWithErrors)
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      userData => {

        for {bool1 <- userRepository.matchUserLoginDetails(userData.username.trim, userData.password.trim)
             bool2 <- userRepository.isUserEnabled(userData.username.trim)
        } yield {
          if (bool1 && bool1 == bool2) {
            Redirect(routes.UserProfileController.getProfileDetails()).withSession("email" -> userData.username)
          } else if (!bool1) {
            Redirect(routes.LoginController.showLoginForm()).flashing("incorrect" -> "Username or password is incorrect.")
          } else {
            Redirect(routes.LoginController.showLoginForm()).flashing("disabled" -> "Your account has been disabled by admin.")
          }
        }
      })
  }

  def forgetPassword(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    forms.forgetPasswordForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Error occurred " + formWithErrors)
        Future.successful(BadRequest(views.html.forgotPassword(formWithErrors)))
      },
      userData => {
        userRepository.updatePassword(userData.email, userData.newPassword).map {
          case false => Redirect(routes.LoginController.showForgetForm()).flashing("emailnotexists" ->
            "Email is not registered in our database.")

          case true => Redirect(routes.LoginController.showLoginForm()).flashing("passwordchanged" ->
            "Password changed successfully. You can log in.")
        }
      })
  }
}
