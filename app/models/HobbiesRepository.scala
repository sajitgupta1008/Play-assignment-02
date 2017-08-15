package models

import com.google.inject.{Inject, Singleton}
import controllers.Hobbies
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


  case class HobbyData(id:Int, hobby: String)

@Singleton
class HobbiesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyRepositoryTable {

  import driver.api._

  def getHobbies: Future[List[String]] = {
        db.run(hobbyQuery.map(_.hobby).to[List].result)
  }

}


trait HobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyQuery: TableQuery[HobbyTable] = TableQuery[HobbyTable]

  class HobbyTable(tag: Tag) extends Table[HobbyData](tag, "hobbytable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def hobby: Rep[String] = column[String]("hobby")

    def * : ProvenShape[HobbyData] = (id, hobby) <> (HobbyData.tupled, HobbyData.unapply)

  }

}

