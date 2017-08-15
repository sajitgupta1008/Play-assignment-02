package models

import org.scalatestplus.play.PlaySpec

class HobbiesRepositoryTest extends PlaySpec {

  val modelsTest = new ModelsTest[HobbiesRepository]

  "HobbiesRepository" should {

    "get hobbies" in {
      val result = modelsTest.result(modelsTest.repository.getHobbies)
      result mustEqual List("reading", "Listening music", "Cricket", "Swimming")
    }
  }
}
