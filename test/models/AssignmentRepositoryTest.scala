package models

import org.scalatestplus.play.PlaySpec


class AssignmentRepositoryTest extends PlaySpec {

  val modelsTest = new ModelsTest[AssignmentRepository]
  private val assignmentData: AssignmentData = AssignmentData(0, "Play","This is assignment")
  "AssignmentRepository" should {

    "add assignment" in {
      val result = modelsTest.result(modelsTest.repository.addAssignment(assignmentData))
      result mustEqual true
    }
    "get assignment" in {
      val result = modelsTest.result(modelsTest.repository.getAssignments())
      result.head mustEqual assignmentData.copy(id=1)
    }
    "delete assignment" in {
      val result = modelsTest.result(modelsTest.repository.deleteAssignment(1))
      result mustEqual true
    }
  }

}
