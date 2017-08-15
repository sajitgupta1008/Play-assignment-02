package controllers

import com.google.inject.Inject
import models.{HobbiesRepository, UserHobbiesRepository, UserProfileData, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserProfileController @Inject()(userRepository: UserRepository, hobbyRepository: HobbiesRepository,
                                      userHobbiesRepository: UserHobbiesRepository, forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def logout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.index()).withNewSession
  }

  def getProfileDetails: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val userName = request.session.get("email").get
    val userDetails: Future[UserProfileData] = userRepository.getUserDetails(userName)
    val userHobbies: Future[List[String]] = userHobbiesRepository.getUserHobbies(userName)
    val isAdmin: Future[Boolean] = userRepository.isAdmin(userName)

    for {details <- userDetails
         hobbies <- userHobbies
         isadmin <- isAdmin}
      yield {
        val profile = Profile(details.firstName, details.middleName, details.lastName,
          details.mobileNo, details.gender, details.age, hobbies)
        Ok(views.html.userProfile(forms.profileForm.fill(profile), hobbies, isadmin))
      }
  }

  def updateDetails(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    forms.profileForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("error occurred" + formWithErrors)
        val userHobbies: Future[List[String]] = userHobbiesRepository.getUserHobbies(request.session.get("email").get)
        val isAdmin: Future[Boolean] = userRepository.isAdmin(request.session.get("email").get)
        for {hobbies <- userHobbies
             isadmin <- isAdmin}
          yield {
            BadRequest(views.html.userProfile(formWithErrors, hobbies, isadmin))
          }
      },

      profile => {
        val updatedDetails: UserProfileData = UserProfileData(profile.name, profile.middleName, profile.lastName,
          profile.mobileNo, profile.gender, profile.age)
        val userUpdated: Future[Boolean] = userRepository.updateUserDetails(request.session.get("email").get, updatedDetails)
        val hobbiesUpdated: Future[Option[Int]] = userHobbiesRepository.updateHobbies(request.session.get("email").get, profile.hobbies)

        userUpdated.flatMap {
          case true => hobbiesUpdated.map {
            case Some(x) if x > 0 => Redirect(routes.UserProfileController.getProfileDetails()).
              flashing("updatesuccess" -> "User details have been updated")
            case _ => InternalServerError("hobbies could not be updated")
          }
          case false => Future.successful(InternalServerError("user details could not be updated"))
        }
      })
  }
}