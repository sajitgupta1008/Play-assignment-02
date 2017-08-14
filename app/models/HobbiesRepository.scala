package models

import com.google.inject.{Inject, Singleton}
import controllers.Hobbies
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class HobbyData(userName: String, hobbyName: String)

@Singleton
class HobbiesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyTable {

  import driver.api._

  def addHobbies(userName: String, hobbies: Hobbies): Future[Boolean] = {

    val hobby1 = if (hobbies.reading) "reading" else ""
    val hobby2 = if (hobbies.music) "music" else ""
    val hobby3 = if (hobbies.movies) "movies" else ""

    val list = List(hobby1,hobby2,hobby3)

    val hobbyDataList: List[HobbyData] = list.filter(_.nonEmpty).map(hobby => HobbyData(userName, hobby))
    db.run(hobbyQuery ++= hobbyDataList).map(_.isDefined)
  }
}


trait HobbyTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyQuery: TableQuery[HobbyMapping] = TableQuery[HobbyMapping]

  class HobbyMapping(tag: Tag) extends Table[HobbyData](tag, "hobbytable") {

    def userName: Rep[String] = column[String]("username")

    def hobbyName: Rep[String] = column[String]("hobbyname")

    override def * : ProvenShape[HobbyData] = (userName, hobbyName) <> (HobbyData.tupled, HobbyData.unapply)
  }

}
