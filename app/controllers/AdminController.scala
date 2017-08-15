package controllers

import com.google.inject.Inject
import models.{AssignmentData, AssignmentRepository, UserData, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AdminController @Inject()(userRepository: UserRepository, assignmentRepository: AssignmentRepository
                                , forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def addAssignment(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    forms.assignmentForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Error occurred " + formWithErrors)
        Future.successful(BadRequest(views.html.assignment(formWithErrors)))
      },
      assignment => {
        assignmentRepository.addAssignment(AssignmentData(0, assignment.title, assignment.description)).map {
          case false => InternalServerError("Failed to add assignment")

          case true => Redirect(routes.AdminController.showAssignmentForm()).flashing("addsuccess" ->
            "Assignment added successfully.")
        }
      })
  }

  def showAssignmentForm(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.assignment(forms.assignmentForm))
  }

  def viewAssignments(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val assignments: Future[List[AssignmentData]] = assignmentRepository.getAssignments()

    val isAdmin = userRepository.isAdmin(request.session.get("email").get)
    isAdmin.flatMap(isadmin=>assignments.map(assignment => Ok(views.html.viewAssignments(assignment, isadmin))))

  }

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    assignmentRepository.deleteAssignment(id).map {
      case true => Redirect(routes.AdminController.viewAssignments())
      case false => InternalServerError("Failed to delete from database")
    }
  }

  def viewUsers(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val list: Future[List[UserData]] = userRepository.getAllUsers

    list.map { userList =>
      Ok(views.html.viewUsersPage(userList.filter(
        _.userName != request.session.get("email").get)))
     /* Ok(views.html.viewUsersPage(userList.filter(
        _.userName != "")))*/
    }
  }

  def enableOrDisableUser(userName: String, isEnabled: Boolean): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    userRepository.enableOrDisableUser(userName, !isEnabled).map {
      case true => Redirect(routes.AdminController.viewUsers())
      case false => InternalServerError("Could not update database")
    }
  }
}
