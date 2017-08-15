package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

case class Hobbies(reading: Boolean, music: Boolean, movies: Boolean)

case class ForgetPassword(email: String, newPassword: String, confirmPassword: String)

case class Profile(name: String, middleName: Option[String], lastName: String
                   , mobileNo: Long, gender: String, age: Int, hobbies: List[String])

case class Assignment(title:String, description:String)

class UserForms {

  val registerForm = Form(mapping(
    "name" -> nonEmptyText.verifying(allLettersConstraint),
    "middleName" -> optional(text).verifying("name can only contain letters",
      value => if (value.isDefined) value.get.matches("""[A-Za-z]+""") else true),
    "lastName" -> nonEmptyText.verifying(allLettersConstraint),
    "userName" -> email,
    "password" -> nonEmptyText.verifying(passwordCheckConstraint),
    "re_enterPassword" -> nonEmptyText.verifying(passwordCheckConstraint),
    "mobileNo" -> longNumber.verifying(checkLengthConstraint),
    "gender" -> nonEmptyText,
    "age" -> number(min = 18, max = 75),
    "hobbies" -> list(text).verifying(hobbyConstraint)
  )(User.apply)(User.unapply)
    verifying("Password fields do not match", user => user.password == user.re_enterPassword)
  )

  val loginForm = Form(mapping(
    "username" -> email,
    "password" -> nonEmptyText
  )(LoginUser.apply)(LoginUser.unapply))


  val forgetPasswordForm: Form[ForgetPassword] = Form(mapping(
    "email" -> email,
    "newPassword" -> nonEmptyText.verifying(passwordCheckConstraint),
    "confirmPassword" -> nonEmptyText
  )(ForgetPassword.apply)(ForgetPassword.unapply)
    verifying("Password fields do not match", forget => forget.newPassword == forget.confirmPassword))

  val profileForm: Form[Profile] = Form(mapping(
    "name" -> nonEmptyText.verifying(allLettersConstraint),
    "middleName" -> optional(text).verifying("name can only contain letters",
      value => if (value.isDefined) value.get.matches("""[A-Za-z]+""") else true),
    "lastName" -> nonEmptyText.verifying(allLettersConstraint),
    "mobileNo" -> longNumber.verifying(checkLengthConstraint),
    "gender" -> nonEmptyText,
    "age" -> number(min = 18, max = 75),
    "hobbies" -> list(text).verifying(hobbyConstraint)
  )(Profile.apply)(Profile.unapply))

  val assignmentForm = Form(mapping(
    "title" -> nonEmptyText,
    "description" -> nonEmptyText
  )(Assignment.apply)(Assignment.unapply))

  def allLettersConstraint: Constraint[String] = {
    Constraint("usernameCheck")(
      name => if (name matches """[A-Za-z]+""")
        Valid
      else
        Invalid(ValidationError("name can only contain letters"))
    )
  }

  def checkLengthConstraint: Constraint[Long] = {
    Constraint("mobile no must be of 10 digits")({
      mobile =>
        if (mobile.toString.length == 10)
          Valid
        else
          Invalid(ValidationError("mobile number must be 10 digits"))
    })
  }

  def hobbyConstraint:Constraint[List[String]] = {
    Constraint("Select atleast one hobby."){
      list =>
        if(list.isEmpty)
          Invalid("Select atleast one hobby.")
        else
          Valid
    }
  }

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

  def passwordCheckConstraint: Constraint[String] = Constraint("constraints.passwordcheck")({
    plainText =>
      val errors = plainText match {
        case allNumbers() => Seq(ValidationError("Password must be alphanumeric"))
        case allLetters() => Seq(ValidationError("Password must be alphanumeric"))
        case x if x.length < 8 => Seq(ValidationError("Password must be of length 8 or greater"))
        case _ => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })
}
