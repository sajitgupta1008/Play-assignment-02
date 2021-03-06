package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller{


  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def loginAction(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
   Redirect(routes.LoginController.showLoginForm())
  }

  def registerAction(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.RegistrationController.showRegisterForm())
  }
}
