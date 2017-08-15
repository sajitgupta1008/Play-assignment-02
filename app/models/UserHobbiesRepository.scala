package models

import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserHobbyData(userName: String, hobbyName: String)

@Singleton
class UserHobbiesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserHobbyTable {

  import driver.api._

  def addHobbies(userName: String, hobbies: List[String]): Future[Option[Int]] = {

    val list: List[UserHobbyData] = hobbies.map(hobby => UserHobbyData(userName, hobby))
    db.run(userHobbyQuery ++= list)
  }

  def getUserHobbies(userName: String): Future[List[String]] = {
    db.run(userHobbyQuery.filter(_.userName === userName).map(_.hobbyName).to[List].result)
  }

  def deleteHobbies(userName: String): Future[Boolean] = {
    db.run(userHobbyQuery.filter(_.userName === userName).delete).map(_ > 0)
  }

  def updateHobbies(userName: String, hobbies: List[String]): Future[Option[Int]] = {
    deleteHobbies(userName).flatMap {
      case true => addHobbies(userName, hobbies)
    }
  }

}

trait UserHobbyTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userHobbyQuery: TableQuery[UserHobbyMapping] = TableQuery[UserHobbyMapping]

  class UserHobbyMapping(tag: Tag) extends Table[UserHobbyData](tag, "userhobbytable") {

    def userName: Rep[String] = column[String]("username")

    def hobbyName: Rep[String] = column[String]("hobbyname")

    override def * : ProvenShape[UserHobbyData] = (userName, hobbyName) <> (UserHobbyData.tupled,
      UserHobbyData.unapply)
  }

}


