package models


import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class AssignmentData(id: Int, title: String, description: String)

class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentTable {

  import driver.api._

  def addAssignment(assignment: AssignmentData): Future[Boolean] = {
    db.run(assignmentQuery += assignment).map(_ > 0)
  }

  def deleteAssignment(id: Int): Future[Boolean] = {
    db.run(assignmentQuery.filter(_.id === id).delete).map(_ > 0)
  }

  def getAssignments(): Future[List[AssignmentData]] = {
    db.run(assignmentQuery.to[List].result)
  }

}

trait AssignmentTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val assignmentQuery: TableQuery[AssignmentMapping] = TableQuery[AssignmentMapping]

  class AssignmentMapping(tag: Tag) extends Table[AssignmentData](tag, "assignmenttable") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")


    override def * : ProvenShape[AssignmentData] = (id, title, description) <> (AssignmentData.tupled,
      AssignmentData.unapply)
  }

}

