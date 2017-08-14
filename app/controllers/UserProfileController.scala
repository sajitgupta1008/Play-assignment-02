package controllers

import com.google.inject.Inject
import models.{HobbiesRepository, UserProfileData, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class UserProfileController @Inject()(userRepository: UserRepository, hobbyRepository: HobbiesRepository, forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def showProfilePage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.userProfile(forms.profileForm))
  }

  def logout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.index())
  }

  def getProfileDetails: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

    val detailsResult: Future[UserProfileData] = userRepository.getUserDetails(request.session.get("email").get)
    //TODO gethobbies

    detailsResult.map { userDetails =>
      val profile = Profile(userDetails.firstName, userDetails.middleName, userDetails.lastName,
        userDetails.mobileNo, userDetails.gender, userDetails.age,Hobbies(true,false,false))

      Ok(views.html.userProfile(forms.profileForm.fill(profile)))
    }

  }

  def updateDetails(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    forms.profileForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("error occurred" + formWithErrors)
        Future.successful(BadRequest(views.html.userProfile(formWithErrors)))
      },

      profile => {
        //TODO hobbies
        val updatedDetails: UserProfileData = UserProfileData(profile.name, profile.middleName, profile.lastName,
          profile.mobileNo, profile.gender, profile.age)
        userRepository.updateUserDetails(request.session.get("email").get, updatedDetails).map {
          case true => Redirect(routes.UserProfileController.getProfileDetails).flashing("update"->"User details have been updated")
          case false =>
           Redirect(routes.UserProfileController.getProfileDetails).flashing("updatefailed"->"Details could not be updated")
        }
        Future.successful(Ok("details changed"))
        //TODO REDIRECT
      })

  }
}