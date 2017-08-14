package models

import com.google.inject.{Inject, Singleton}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserData(id: Int, firstName: String, middleName: Option[String], lastName: String, userName: String, password: String, mobileNo: Long,
                    gender: String, age: Int, isAdmin: Boolean = false, isEnabled: Boolean = true)

case class UserProfileData(firstName: String, middleName: Option[String], lastName: String, mobileNo: Long,
                           gender: String, age: Int)

@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserTable {

  import driver.api._

  def addUser(user: UserData): Future[Boolean] = {

    db.run(userQuery += user.copy(password = BCrypt.hashpw(user.password, BCrypt.gensalt()))).map(_ > 0)
  }

  def checkUserExists(name: String): Future[Boolean] = {
    val usersList = db.run(userQuery.filter(_.userName === name).to[List].result)
    usersList.map(_.nonEmpty)
  }

  def matchUserLoginDetails(userName: String, password: String): Future[Boolean] = {

    val userList: Future[List[UserData]] = db.run(userQuery.filter(user => user.userName === userName).to[List].result)
    userList.map {
      case users if users.isEmpty => false
      case users => BCrypt.checkpw(password, users.head.password)
    }
  }

  def isUserEnabled(userName: String): Future[Boolean] = {
    db.run(userQuery.filter(user => user.userName === userName && user.isEnabled).to[List].result).map(_.nonEmpty)
  }

  def updatePassword(userName: String, password: String): Future[Boolean] = {
    checkUserExists(userName).flatMap {

      case true => db.run(userQuery.filter(_.userName === userName).map(_.password)
        .update(BCrypt.hashpw(password, BCrypt.gensalt()))).map(_ > 0)

      case false => Future.successful(false)
    }
  }

  def updateUserDetails(userName: String, updatedData: UserProfileData): Future[Boolean] = {

    db.run(userQuery.filter(_.userName === userName).map(user => (user.firstName, user.middleName, user.lastName,
      user.mobileNo, user.gender, user.age)).update(updatedData.firstName, updatedData.middleName, updatedData.lastName, updatedData.mobileNo,
      updatedData.gender, updatedData.age)).map(_ > 0)
  }

  def getUserDetails(userName: String): Future[UserProfileData] = {
    val userDetails: Future[List[UserData]] = db.run(userQuery.filter(_.userName === userName).to[List].result)
    userDetails.map {
      users =>
        val userData: UserData = users.head
        UserProfileData(userData.firstName, userData.middleName, userData.lastName, userData.mobileNo,
          userData.gender, userData.age)
    }
  }
}


trait UserTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserMapping] = TableQuery[UserMapping]

  class UserMapping(tag: Tag) extends Table[UserData](tag, "userdatatable") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def userName: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def mobileNo: Rep[Long] = column[Long]("mobileno")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")

    override def * : ProvenShape[UserData] = (id, firstName, middleName, lastName, userName, password, mobileNo, gender, age, isAdmin, isEnabled) <> (UserData.tupled,
      UserData.unapply)
  }

}


